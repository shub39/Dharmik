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
package com.shub39.dharmik.shared.logic.data

import com.shub39.dharmik.shared.core.domain.AudioVariant
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

/**
 * Canonical on-device layout for downloaded audio.
 *
 * ```
 * <filesDir>/audio/
 *   CHAPn/001_mool.ogg | .mp3
 *   CHAPn/001_hindi.ogg | .mp3
 *   CHAPn/001_english.ogg | .mp3
 *   gita-audio.zip.part
 * ```
 *
 * Per-verse downloads write `.mp3` files, the bulk archive writes `.ogg` files; both live in the
 * same `CHAPn/NNN_<variant>` scheme so a bulk download can cleanly replace the per-verse set.
 */
@Single
class AudioStore(private val storage: AudioStorage) {

    private val fs: FileSystem = FileSystem.SYSTEM

    val root: Path
        get() = storage.audioDirPath().toPath()

    val zipFile: Path
        get() = root / ZIP_NAME

    val zipPartFile: Path
        get() = root / "$ZIP_NAME.part"

    /** Existing local file for a track, preferring the bulk (`.ogg`) copy, or `null` if absent. */
    fun localFile(chapter: Long, verse: Long, variant: AudioVariant): Path? {
        val dir = chapterDir(chapter)
        val name = fileName(verse, variant)
        val ogg = dir / "$name.ogg"
        if (fs.exists(ogg)) return ogg
        val mp3 = dir / "$name.mp3"
        if (fs.exists(mp3)) return mp3
        return null
    }

    /** Path a freshly downloaded/extracted track should be written to. */
    fun targetFile(chapter: Long, verse: Long, variant: AudioVariant, extension: String): Path =
        chapterDir(chapter) / "${fileName(verse, variant)}.$extension"

    fun fileUri(path: Path): String = storage.toFileUri(path.toString())

    fun chapterDir(chapter: Long): Path = root / "CHAP$chapter"

    fun ensureChapterDir(chapter: Long) {
        fs.createDirectories(chapterDir(chapter))
    }

    fun ensureRoot() {
        fs.createDirectories(root)
    }

    /** Removes the whole audio store (used by "delete" and before a bulk replace). */
    fun deleteAll() {
        if (fs.exists(root)) fs.deleteRecursively(root)
    }

    private fun fileName(verse: Long, variant: AudioVariant): String =
        "${verse.toString().padStart(3, '0')}_${variant.key}"

    companion object {
        const val ZIP_NAME = "gita-audio.zip"
    }
}
