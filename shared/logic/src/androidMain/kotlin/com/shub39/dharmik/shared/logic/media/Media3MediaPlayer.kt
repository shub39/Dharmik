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

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.MediaPlayerError
import com.shub39.dharmik.shared.core.media.MediaPlayerState
import com.shub39.dharmik.shared.core.media.MediaSource
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** [MediaPlayer] implementation backed by AndroidX Media3's ExoPlayer. */
class Media3MediaPlayer(private val context: Context) : MediaPlayer {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var player: ExoPlayer? = null

    private val _state = MutableStateFlow<MediaPlayerState>(MediaPlayerState.Idle)
    override val state: StateFlow<MediaPlayerState> = _state.asStateFlow()

    private val _position = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _position.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _error = MutableStateFlow<MediaPlayerError?>(null)
    override val error: StateFlow<MediaPlayerError?> = _error.asStateFlow()

    override suspend fun setSource(source: MediaSource) =
        withContext(Dispatchers.Main) {
            val uri =
                when (source) {
                    is MediaSource.Url -> source.url.toUri()
                    is MediaSource.Asset -> "asset:///${source.assetName}".toUri()
                    is MediaSource.Resource ->
                        "android.resource://${context.packageName}/${source.resourceId}".toUri()
                }

            val exo = ensurePlayer()
            _error.value = null
            _state.value = MediaPlayerState.Preparing
            exo.setMediaItem(MediaItem.fromUri(uri))
            exo.prepare()
        }

    override suspend fun play() = withContext(Dispatchers.Main) { ensurePlayer().play() }

    override suspend fun pause() {
        withContext(Dispatchers.Main) { player?.pause() }
    }

    override suspend fun stop() =
        withContext(Dispatchers.Main) {
            player?.stop()
            _state.value = MediaPlayerState.Stopped
        }

    override suspend fun seekTo(position: Long) {
        withContext(Dispatchers.Main) { player?.seekTo(position) }
    }

    override fun release() {
        scope.cancel()
        player?.release()
        player = null
        _state.value = MediaPlayerState.Idle
    }

    private fun ensurePlayer(): ExoPlayer {
        player?.let {
            return it
        }

        val exo = ExoPlayer.Builder(context).build()
        exo.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _state.value =
                        when (playbackState) {
                            Player.STATE_IDLE -> MediaPlayerState.Idle
                            Player.STATE_BUFFERING -> MediaPlayerState.Preparing
                            Player.STATE_READY ->
                                if (exo.isPlaying) MediaPlayerState.Playing
                                else MediaPlayerState.Prepared
                            Player.STATE_ENDED -> MediaPlayerState.Completed
                            else -> MediaPlayerState.Idle
                        }
                    _duration.value = exo.duration.coerceAtLeast(0L)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        _state.value = MediaPlayerState.Playing
                    } else if (_state.value == MediaPlayerState.Playing) {
                        _state.value = MediaPlayerState.Prepared
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    val mapped = MediaPlayerError.PlaybackError(error.message ?: "Playback error")
                    _error.value = mapped
                    _state.value = MediaPlayerState.Error(mapped)
                }
            }
        )

        player = exo
        scope.launch {
            while (isActive) {
                _position.value = exo.currentPosition
                delay(500.milliseconds)
            }
        }
        return exo
    }
}
