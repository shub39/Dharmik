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

import kotlinx.coroutines.flow.StateFlow

/**
 * Platform-agnostic media player interface. Actual implementations provided per platform
 * (Android/Desktop).
 */
interface MediaPlayer {
    val state: StateFlow<MediaPlayerState>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val error: StateFlow<MediaPlayerError?>

    suspend fun setSource(source: MediaSource)

    suspend fun play()

    suspend fun pause()

    suspend fun stop()

    suspend fun seekTo(position: Long)

    fun release()
}

/** Media source types supported by the player. */
sealed interface MediaSource {
    data class Url(val url: String) : MediaSource

    data class Asset(val assetName: String) : MediaSource

    data class Resource(val resourceId: Int) : MediaSource
}

/** Media player states. */
sealed interface MediaPlayerState {
    object Idle : MediaPlayerState

    object Preparing : MediaPlayerState

    object Prepared : MediaPlayerState

    object Playing : MediaPlayerState

    object Paused : MediaPlayerState

    object Stopped : MediaPlayerState

    object Completed : MediaPlayerState

    data class Error(val error: MediaPlayerError) : MediaPlayerState
}

/** Media player errors. */
sealed interface MediaPlayerError {
    val message: String

    data class SourceError(override val message: String) : MediaPlayerError

    data class PreparationError(override val message: String) : MediaPlayerError

    data class PlaybackError(override val message: String) : MediaPlayerError

    data class UnknownError(override val message: String) : MediaPlayerError
}

/** Factory for creating platform-specific MediaPlayer instances. */
interface MediaPlayerFactory {
    fun create(): MediaPlayer
}
