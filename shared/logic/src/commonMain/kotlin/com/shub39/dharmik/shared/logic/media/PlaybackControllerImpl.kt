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

import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.core.domain.AudioRepository
import com.shub39.dharmik.shared.core.domain.AudioTrack
import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.PlaybackController
import com.shub39.dharmik.shared.core.domain.PlaybackState
import com.shub39.dharmik.shared.core.domain.Playlist
import com.shub39.dharmik.shared.core.domain.PlaylistSource
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.MediaSource
import com.shub39.dharmik.shared.core.media.MediaTrack
import com.shub39.dharmik.shared.logic.data.AudioStore
import com.shub39.dharmik.shared.logic.data.GitaAudioUrls
import dharmik.shared.logic.generated.resources.Res
import dharmik.shared.logic.generated.resources.chapter_template
import dharmik.shared.logic.generated.resources.verses_alt_template
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Single

/**
 * App-scoped playback session.
 *
 * Owns the notion of "what is playing": it turns a [Playlist] into the queue the platform player
 * receives (preferring files that are already on device), keeps the downloaded copy of the tracks
 * around for offline listening and mirrors the player back into [PlaybackState].
 *
 * The session outlives every screen, which is what lets the Player tab show what is playing and
 * keeps the notification in sync.
 */
@Single(binds = [PlaybackController::class])
class PlaybackControllerImpl(
    private val player: MediaPlayer,
    private val audioRepository: AudioRepository,
    private val store: AudioStore,
    private val analytics: AnalyticsWrapper,
) : PlaybackController {

    // Application-scoped: lives as long as the singleton and is independent of any UI or service.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    /**
     * Index of the first queued track inside the playlist. It is non zero while
     * [PlaybackState.autoAdvance] is off, because only the current track is queued then.
     */
    private var queueOffset = 0

    private var prefetchJob: Job? = null

    init {
        scope.launch {
            player.state
                .onEach { state -> _state.update { it.copy(playerState = state) } }
                .collect {}
        }

        // Keeping the index and the offline prefetch together means the prefetch always sees the
        // track that just started.
        scope.launch {
            player.currentIndex
                .onEach { index ->
                    val playlistIndex = queueOffset + index
                    _state.update { it.copy(index = playlistIndex) }
                    prefetch(playlistIndex)
                }
                .collect {}
        }

        scope.launch {
            player.currentPosition
                .onEach { position -> _state.update { it.copy(positionMillis = position) } }
                .collect {}
        }

        scope.launch {
            player.duration
                .onEach { duration -> _state.update { it.copy(durationMillis = duration) } }
                .collect {}
        }
    }

    override suspend fun play(
        playlist: Playlist,
        variant: AudioVariant,
        startIndex: Int,
        autoAdvance: Boolean,
    ) {
        if (playlist.tracks.isEmpty()) return

        val index = startIndex.coerceIn(0, playlist.tracks.lastIndex)
        _state.update { it.copy(playlist = playlist, variant = variant, autoAdvance = autoAdvance) }

        enqueue(playlist, variant, index, playWhenReady = true)

        analytics.trackEvent(
            AnalyticsEvent.PLAYLIST_STARTED,
            mapOf(
                "source" to playlist.source.key,
                "size" to playlist.tracks.size,
                "index" to index,
                "auto_advance" to autoAdvance,
            ),
        )
    }

    override suspend fun playPause() {
        if (_state.value.isPlaying) player.pause() else player.play()
    }

    override suspend fun pause() {
        player.pause()
    }

    override suspend fun stop() {
        player.stop()
    }

    override suspend fun next() {
        val state = _state.value
        val playlist = state.playlist ?: return
        val target = state.index + 1
        if (target > playlist.tracks.lastIndex) return

        if (state.autoAdvance) {
            player.seekToIndex(target)
        } else {
            enqueue(playlist, state.variant, target, playWhenReady = state.isPlaying)
        }
    }

    override suspend fun previous() {
        val state = _state.value
        val playlist = state.playlist ?: return

        // Restart the track that is playing, like every other player does.
        if (state.positionMillis > RESTART_THRESHOLD_MILLIS) {
            player.seekTo(0L)
            return
        }

        val target = state.index - 1
        if (target < 0) {
            player.seekTo(0L)
            return
        }

        if (state.autoAdvance) {
            player.seekToIndex(target)
        } else {
            enqueue(playlist, state.variant, target, playWhenReady = state.isPlaying)
        }
    }

    override suspend fun select(index: Int) {
        val state = _state.value
        val playlist = state.playlist ?: return
        if (index !in playlist.tracks.indices) return

        enqueue(playlist, state.variant, index, playWhenReady = true)
    }

    override suspend fun seekTo(positionMillis: Long) {
        player.seekTo(positionMillis)
    }

    override suspend fun setVariant(variant: AudioVariant) {
        val state = _state.value
        if (state.variant == variant) return

        _state.update { it.copy(variant = variant) }

        analytics.trackEvent(AnalyticsEvent.AUDIO_VARIANT_CHANGED, mapOf("variant" to variant.key))

        val playlist = state.playlist ?: return
        // Switching language restarts the current verse with the new track.
        requeue(playlist, variant, state, restart = true)
    }

    override suspend fun setAutoAdvance(enabled: Boolean) {
        val state = _state.value
        if (state.autoAdvance == enabled) return

        _state.update { it.copy(autoAdvance = enabled) }

        val playlist = state.playlist ?: return
        requeue(playlist, state.variant, state, restart = false)
    }

    /** Replaces the queue with [playlist] starting at [index]. */
    private suspend fun enqueue(
        playlist: Playlist,
        variant: AudioVariant,
        index: Int,
        playWhenReady: Boolean,
    ) {
        val autoAdvance = _state.value.autoAdvance
        val tracks = if (autoAdvance) playlist.tracks else listOf(playlist.tracks[index])
        queueOffset = if (autoAdvance) 0 else index

        _state.update { it.copy(index = index, isResolving = true) }

        val queue = tracks.map { it.toMediaTrack(playlist.source, variant) }

        runCatching {
            player.setQueue(queue, if (autoAdvance) index else 0, playWhenReady = playWhenReady)
        }

        _state.update { it.copy(isResolving = false) }
    }

    /**
     * Re-enqueues the current playlist at the current track.
     *
     * @param restart start the track from the beginning instead of the current position.
     */
    private suspend fun requeue(
        playlist: Playlist,
        variant: AudioVariant,
        state: PlaybackState,
        restart: Boolean,
    ) {
        val position = if (restart) 0L else state.positionMillis

        enqueue(playlist, variant, state.index, playWhenReady = state.isPlaying)

        if (position > 0L) player.seekTo(position)
    }

    /**
     * Downloads the track that plays next (and the current one, if it was streamed) so that a later
     * listen is served from the local copy.
     */
    private fun prefetch(index: Int) {
        val state = _state.value
        val playlist = state.playlist ?: return
        val variant = state.variant

        val offset = queueOffset

        prefetchJob?.cancel()
        prefetchJob =
            scope.launch {
                // The current track was most likely streamed, store it for offline listening. The
                // upcoming one is also swapped in as a local file so that it does not have to be
                // streamed again.
                listOf(index, index + 1).distinct().forEach { playlistIndex ->
                    val track = playlist.tracks.getOrNull(playlistIndex) ?: return@forEach

                    // Tracks that are already on device were queued as local files.
                    if (store.localFile(track.chapter, track.verse, variant) != null) {
                        return@forEach
                    }

                    val uri =
                        runCatching { audioRepository.resolve(track.chapter, track.verse, variant) }
                            .getOrNull() ?: return@forEach
                    val queueIndex = playlistIndex - offset

                    // Replacing the item that is playing would restart it.
                    if (queueIndex < 0 || queueIndex == player.currentIndex.value) {
                        return@forEach
                    }

                    runCatching { player.replaceTrack(queueIndex, MediaSource.Url(uri)) }
                }
            }
    }

    private suspend fun AudioTrack.toMediaTrack(
        source: PlaylistSource,
        variant: AudioVariant,
    ): MediaTrack {
        val chapterTitle = getString(Res.string.chapter_template, chapter)
        val verseTitle = getString(Res.string.verses_alt_template, verse)

        return MediaTrack(
            id = "$chapter:$verse:${variant.key}",
            source =
                store.localFile(chapter, verse, variant)?.let { MediaSource.Url(store.fileUri(it)) }
                    ?: MediaSource.Url(GitaAudioUrls.url(chapter, verse, variant)),
            title = verseTitle,
            subtitle = if (source is PlaylistSource.Collection) source.name else chapterTitle,
        )
    }

    private companion object {
        const val RESTART_THRESHOLD_MILLIS = 3_000L
    }
}
