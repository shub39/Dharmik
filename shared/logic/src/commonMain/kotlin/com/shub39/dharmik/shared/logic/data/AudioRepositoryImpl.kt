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

import com.shub39.dharmik.shared.core.domain.AudioRepository
import com.shub39.dharmik.shared.core.domain.AudioVariant
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import org.koin.core.annotation.Single

/**
 * Download-before-play: returns the local `file://` URI of a verse's track, downloading it from the
 * legacy Gita Supersite host the first time. If the download fails (e.g. offline) it falls back to
 * the remote URL so the player can stream it.
 */
@Single(binds = [AudioRepository::class])
class AudioRepositoryImpl(private val store: AudioStore, private val client: HttpClient) :
    AudioRepository {

    private val fs: FileSystem = FileSystem.SYSTEM

    override suspend fun resolve(chapter: Long, verse: Long, variant: AudioVariant): String {
        store.localFile(chapter, verse, variant)?.let {
            return store.fileUri(it)
        }

        return try {
            download(chapter, verse, variant)
            store.localFile(chapter, verse, variant)?.let { store.fileUri(it) }
                ?: GitaAudioUrls.url(chapter, verse, variant)
        } catch (e: Exception) {
            GitaAudioUrls.url(chapter, verse, variant)
        }
    }

    private suspend fun download(chapter: Long, verse: Long, variant: AudioVariant) {
        val bytes = client.get(GitaAudioUrls.url(chapter, verse, variant)).bodyAsBytes()

        store.ensureChapterDir(chapter)
        val target = store.targetFile(chapter, verse, variant, "mp3")
        val tmp = target.parent!! / "${target.name}.part"

        withContext(Dispatchers.Default) {
            fs.write(tmp) { write(bytes) }
            fs.atomicMove(tmp, target)
        }
    }
}
