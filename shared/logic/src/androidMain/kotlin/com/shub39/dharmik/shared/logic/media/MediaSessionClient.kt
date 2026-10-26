/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.logic.media

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.MediaPlayerError
import com.shub39.dharmik.shared.core.media.MediaPlayerState
import com.shub39.dharmik.shared.core.media.MediaSource
import com.shub39.dharmik.shared.core.media.MediaTrack
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * [MediaPlayer] that drives the app's single [PlaybackService] through a [MediaController].
 *
 * The queue and the actual engine live in the service, which is what makes background playback, the
 * media notification and lock screen controls work. This class only forwards commands and mirrors
 * the session state back into flows.
 *
 * The controller is connected lazily on the first command and reconnected on demand if the session
 * goes away (for example when the service was stopped by the system).
 */
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
class MediaSessionClient(private val context: Context) : MediaPlayer {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val mainExecutor =
        java.util.concurrent.Executor { command -> mainHandler.post(command) }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Main thread confined.
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _state = MutableStateFlow<MediaPlayerState>(MediaPlayerState.Idle)
    override val state: StateFlow<MediaPlayerState> = _state.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _position = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _position.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _error = MutableStateFlow<MediaPlayerError?>(null)
    override val error: StateFlow<MediaPlayerError?> = _error.asStateFlow()

    private val controllerListener =
        object : MediaController.Listener {
            override fun onDisconnected(controller: MediaController) {
                this@MediaSessionClient.controller = null
                this@MediaSessionClient.controllerFuture = null
                _state.value = MediaPlayerState.Idle
            }
        }

    init {
        scope.launch {
            while (isActive) {
                controller?.let { current ->
                    _position.value = current.currentPosition.coerceAtLeast(0L)
                    _duration.value = current.duration.coerceAtLeast(0L)
                }
                delay(POSITION_POLL_INTERVAL)
            }
        }
    }

    override suspend fun setQueue(
        tracks: List<MediaTrack>,
        startIndex: Int,
        playWhenReady: Boolean,
    ) {
        if (tracks.isEmpty()) return

        val current = awaitController()
        _error.value = null
        _state.value = MediaPlayerState.Preparing

        current.setMediaItems(
            tracks.map { it.toMediaItem() },
            startIndex.coerceIn(0, tracks.lastIndex),
            C.TIME_UNSET,
        )
        current.prepare()

        if (playWhenReady) current.play()
    }

    override suspend fun replaceTrack(index: Int, source: MediaSource) {
        val current = awaitController()
        if (index !in 0 until current.mediaItemCount) return
        // Never touch the item that is currently playing, replacing it would restart playback.
        if (index == current.currentMediaItemIndex) return

        current.replaceMediaItem(
            index,
            current.getMediaItemAt(index).buildUpon().setUri(source.toUri(context)).build(),
        )
    }

    override suspend fun play() {
        awaitController().play()
    }

    override suspend fun pause() {
        withContext(Dispatchers.Main.immediate) { controller?.pause() }
    }

    override suspend fun stop() {
        withContext(Dispatchers.Main.immediate) {
            controller?.stop()
            _state.value = MediaPlayerState.Stopped
        }
    }

    override suspend fun seekTo(position: Long) {
        withContext(Dispatchers.Main.immediate) {
            controller?.seekTo(position.coerceAtLeast(0L))
            _position.value = position.coerceAtLeast(0L)
        }
    }

    override suspend fun seekToIndex(index: Int) {
        val current = awaitController()
        if (index !in 0 until current.mediaItemCount) return

        current.seekTo(index, C.TIME_UNSET)
    }

    override suspend fun skipToNext() {
        awaitController().let { if (it.hasNextMediaItem()) it.seekToNextMediaItem() }
    }

    override suspend fun skipToPrevious() {
        awaitController().let { if (it.hasPreviousMediaItem()) it.seekToPreviousMediaItem() }
    }

    override fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        controllerFuture = null
        _state.value = MediaPlayerState.Idle
    }

    /** Connects the controller if needed and returns it, always on the main thread. */
    private suspend fun awaitController(): MediaController =
        withContext(Dispatchers.Main.immediate) {
            controller?.let {
                return@withContext it
            }

            val future =
                controllerFuture
                    ?: MediaController.Builder(
                            context,
                            SessionToken(
                                context,
                                ComponentName(context, PlaybackService::class.java),
                            ),
                        )
                        .setApplicationLooper(Looper.getMainLooper())
                        .setListener(controllerListener)
                        .buildAsync()
                        .also { controllerFuture = it }

            suspendCancellableCoroutine { continuation ->
                future.addListener(
                    {
                        val connected = runCatching { future.get() }.getOrNull()
                        if (connected == null) {
                            continuation.resumeWithException(
                                IllegalStateException("Media session is unavailable")
                            )
                        } else {
                            attach(connected)
                            continuation.resume(connected)
                        }
                    },
                    mainExecutor,
                )
            }
        }

    private fun attach(controller: MediaController) {
        this.controller = controller
        _currentIndex.value = controller.currentMediaItemIndex
        _state.value = controller.toMediaPlayerState()

        controller.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _state.value = controller.toMediaPlayerState()
                    _duration.value = controller.duration.coerceAtLeast(0L)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.value = controller.toMediaPlayerState()
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    _currentIndex.value = controller.currentMediaItemIndex
                    _duration.value = controller.duration.coerceAtLeast(0L)
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    if (controller.mediaItemCount == 0) _currentIndex.value = 0
                }

                override fun onPlayerError(error: PlaybackException) {
                    val mapped = MediaPlayerError.PlaybackError(error.message ?: "Playback error")
                    _error.value = mapped
                    _state.value = MediaPlayerState.Error(mapped)
                }
            }
        )
    }

    private fun Player.toMediaPlayerState(): MediaPlayerState =
        when (playbackState) {
            Player.STATE_IDLE -> MediaPlayerState.Idle
            Player.STATE_BUFFERING -> MediaPlayerState.Preparing
            Player.STATE_READY ->
                if (isPlaying) MediaPlayerState.Playing else MediaPlayerState.Prepared
            Player.STATE_ENDED -> MediaPlayerState.Completed
            else -> MediaPlayerState.Idle
        }

    private fun MediaTrack.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(id)
            .setUri(source.toUri(context))
            .setMediaMetadata(MediaMetadata.Builder().setTitle(title).setArtist(subtitle).build())
            .build()

    private fun MediaSource.toUri(context: Context): Uri =
        when (this) {
            is MediaSource.Url -> url.toUri()
            is MediaSource.Asset -> "asset:///$assetName".toUri()
            is MediaSource.Resource ->
                "android.resource://${context.packageName}/$resourceId".toUri()
        }

    private companion object {
        val POSITION_POLL_INTERVAL = 500.milliseconds
    }
}
