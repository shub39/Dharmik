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
package com.shub39.dharmik.shared.core.domain

import com.shub39.dharmik.shared.core.media.MediaPlayerState
import kotlinx.coroutines.flow.StateFlow

/** A single playable verse. */
data class AudioTrack(val chapter: Long, val verse: Long)

/**
 * Where a [Playlist] came from.
 *
 * Only identifiers travel here: the chapter title is localised by the UI and a collection name is
 * user provided, so no display string is baked into the model.
 */
sealed interface PlaylistSource {
    /** All verses of [chapter], listened to chapter wise. */
    data class Chapter(val chapter: Long) : PlaylistSource

    /** The verses of a user collection, which doubles as a custom playlist. */
    data class Collection(val id: Long, val name: String) : PlaylistSource

    /** Snake case token used as an analytics property. */
    val key: String
        get() =
            when (this) {
                is Chapter -> "chapter"
                is Collection -> "collection"
            }
}

/** A playable list of verse tracks. */
data class Playlist(val source: PlaylistSource, val tracks: List<AudioTrack>)

/** All verses of [chapter] as a playlist, without parsing the chapter file. */
fun chapterPlaylist(chapter: Long): Playlist {
    val count = slokaNumbers.getOrNull((chapter - 1).toInt()) ?: 0

    return Playlist(
        source = PlaylistSource.Chapter(chapter),
        tracks = (1..count).map { AudioTrack(chapter = chapter, verse = it.toLong()) },
    )
}

/** Snapshot of the single app-wide playback session. */
data class PlaybackState(
    val playlist: Playlist? = null,
    /** Index of the current track inside the full playlist. */
    val index: Int = 0,
    val playerState: MediaPlayerState = MediaPlayerState.Idle,
    val positionMillis: Long = 0L,
    val durationMillis: Long = 0L,
    val variant: AudioVariant = AudioVariant.MOOL,
    /** Whether playback continues into the next track of the playlist when one finishes. */
    val autoAdvance: Boolean = true,
    /** A track is being resolved (downloaded) before it can be played. */
    val isResolving: Boolean = false,
) {
    val currentTrack: AudioTrack?
        get() = playlist?.tracks?.getOrNull(index)

    val isPlaying: Boolean
        get() = playerState == MediaPlayerState.Playing
}

/**
 * Owns the app-wide playback session: builds playlists, resolves their tracks to local or remote
 * sources and forwards commands to the platform player.
 *
 * There is exactly one implementation for the whole app, so starting a playlist from any screen
 * simply replaces the session and the Player screen always shows what is currently playing.
 */
interface PlaybackController {
    val state: StateFlow<PlaybackState>

    /**
     * Replaces the session with [playlist] and starts at [startIndex].
     *
     * @param variant which of the three tracks of a verse is played.
     * @param startIndex index inside [playlist] to start from.
     * @param autoAdvance whether playback continues with the next track.
     */
    suspend fun play(
        playlist: Playlist,
        variant: AudioVariant,
        startIndex: Int = 0,
        autoAdvance: Boolean = true,
    )

    suspend fun playPause()

    suspend fun pause()

    suspend fun stop()

    suspend fun next()

    suspend fun previous()

    /** Jumps to [index] inside the current playlist and plays it. */
    suspend fun select(index: Int)

    suspend fun seekTo(positionMillis: Long)

    /** Switches the audio variant, keeping the current track and position. */
    suspend fun setVariant(variant: AudioVariant)

    /** Enables or disables auto advancing, keeping the current track and position. */
    suspend fun setAutoAdvance(enabled: Boolean)
}
