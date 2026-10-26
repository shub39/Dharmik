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
package com.shub39.dharmik.shared.core.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Inert, platform-agnostic implementation of [MediaPlayer].
 *
 * Every operation is a no-op that never throws: the player always reports [MediaPlayerState.Idle],
 * an index/position/duration of `0` and a `null` error. This is a single common stub so that
 * `:shared:core` needs neither an `expect`/`actual` factory nor any platform (`androidx.media3`,
 * `chaintech.videoplayer`, ...) dependency.
 *
 * [setQueue] intentionally does **not** auto-play.
 */
class NoOpMediaPlayer : MediaPlayer {
    private val _state = MutableStateFlow<MediaPlayerState>(MediaPlayerState.Idle)
    override val state: StateFlow<MediaPlayerState> = _state

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration

    private val _error = MutableStateFlow<MediaPlayerError?>(null)
    override val error: StateFlow<MediaPlayerError?> = _error

    override suspend fun setQueue(
        tracks: List<MediaTrack>,
        startIndex: Int,
        playWhenReady: Boolean,
    ) {
        // Inert: stays Idle, never prepares, never plays.
        _currentIndex.value = startIndex
        _state.value = MediaPlayerState.Idle
    }

    override suspend fun replaceTrack(index: Int, source: MediaSource) {
        // Inert.
    }

    override suspend fun play() {
        // Inert.
    }

    override suspend fun pause() {
        // Inert.
    }

    override suspend fun stop() {
        // Inert.
    }

    override suspend fun seekTo(position: Long) {
        // Inert.
    }

    override suspend fun seekToIndex(index: Int) {
        // Inert.
    }

    override suspend fun skipToNext() {
        // Inert.
    }

    override suspend fun skipToPrevious() {
        // Inert.
    }

    override fun release() {
        // Inert.
    }
}

/**
 * Common [MediaPlayerFactory] that always yields a [NoOpMediaPlayer].
 *
 * Kept in `:shared:core` so the module is self-contained without any `expect`/`actual` media
 * factory or platform media dependency.
 */
class NoOpMediaPlayerFactory : MediaPlayerFactory {
    override fun create(): MediaPlayer = NoOpMediaPlayer()
}
