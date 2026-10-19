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

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * The three audio tracks available for every verse.
 *
 * [key] is the canonical token used for on-device file names (`CHAPn/NNN_<key>.<ext>`), while
 * [bulkName] matches the file naming inside the `gita-audio.zip` release asset.
 */
enum class AudioVariant(val key: String, val bulkName: String) {
    MOOL("mool", "mool_sloka"),
    HINDI("hindi", "hindi_translation"),
    ENGLISH("english", "english_translations"),
}

/** Maps the currently selected verse card language to the audio variant to play. */
val VerseCardState.audioVariant: AudioVariant
    get() =
        when (this) {
            VerseCardState.ENGLISH -> AudioVariant.ENGLISH
            VerseCardState.HINDI -> AudioVariant.HINDI
            VerseCardState.SANSKRIT -> AudioVariant.MOOL
        }

/**
 * Manifest published alongside `gita-audio.zip` on every GitHub Release.
 *
 * The archive is treated as immutable: [version] is a content fingerprint computed in CI and is
 * stored on device purely as a marker that the set has been downloaded. It is never re-compared
 * against the remote manifest to look for updates.
 */
@Serializable
data class AudioManifest(
    val version: String,
    val file: String = "gita-audio.zip",
    val sizeBytes: Long = 0L,
    val sha256: String = "",
    val url: String = "",
)

/** State of the optional whole-set ("bulk") audio download. */
sealed interface BulkAudioState {
    /** Manifest is being fetched / local state is being resolved. */
    data object Checking : BulkAudioState

    /** Nothing downloaded yet. */
    data class NotDownloaded(val remoteVersion: String?, val sizeBytes: Long?) : BulkAudioState

    /** Archive is being downloaded. */
    data class Downloading(val bytesRead: Long, val totalBytes: Long) : BulkAudioState {
        val progress: Float
            get() = if (totalBytes > 0L) (bytesRead.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
    }

    /** Archive is being verified and extracted. */
    data object Extracting : BulkAudioState

    /** The whole set is available locally. */
    data class Downloaded(val version: String) : BulkAudioState

    data class Failed(val message: String?) : BulkAudioState
}

/**
 * Owns the optional bulk audio download from the GitHub Release asset: exposes progress and lets
 * the UI refresh (check for a new version), start, cancel or delete it.
 */
interface AudioBulkRepo {
    val state: Flow<BulkAudioState>

    /** Fetch the remote manifest and reconcile it with what is on disk. */
    suspend fun refresh()

    suspend fun start()

    suspend fun cancel()

    suspend fun delete()
}

/**
 * Resolves the playable URI for a verse's audio track.
 *
 * Returns a local `file://` URI when the track is already stored (or is part of the downloaded bulk
 * set); otherwise it downloads the track from the legacy Gita Supersite host first. If that
 * download fails (e.g. offline) it falls back to returning the remote `https://` URL so the player
 * can stream.
 */
interface AudioRepository {
    suspend fun resolve(chapter: Long, verse: Long, variant: AudioVariant): String
}
