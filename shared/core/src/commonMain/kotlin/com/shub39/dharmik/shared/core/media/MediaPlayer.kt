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
 * One playable entry of a queue.
 *
 * [title]/[subtitle] travel with the track because the platform media session has to render them
 * outside the app (notification, lock screen, Bluetooth displays).
 */
data class MediaTrack(
    val id: String,
    val source: MediaSource,
    val title: String,
    val subtitle: String? = null,
)

/**
 * Platform-agnostic media player interface. Actual implementations provided per platform
 * (Android/Desktop).
 *
 * The player owns a queue rather than a single source: a "single track" is simply a queue of one,
 * so playlist navigation and background media controls work the same everywhere.
 */
interface MediaPlayer {
    val state: StateFlow<MediaPlayerState>
    val currentIndex: StateFlow<Int>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val error: StateFlow<MediaPlayerError?>

    /** Replaces the queue and starts preparing it, optionally starting playback. */
    suspend fun setQueue(
        tracks: List<MediaTrack>,
        startIndex: Int = 0,
        playWhenReady: Boolean = true,
    )

    /**
     * Swaps the source of an already queued track, used to hand the player the downloaded local
     * file of a track that was queued with its remote URL. Implementations must not interrupt
     * playback of the current track.
     */
    suspend fun replaceTrack(index: Int, source: MediaSource)

    suspend fun play()

    suspend fun pause()

    suspend fun stop()

    suspend fun seekTo(position: Long)

    suspend fun seekToIndex(index: Int)

    suspend fun skipToNext()

    suspend fun skipToPrevious()

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
