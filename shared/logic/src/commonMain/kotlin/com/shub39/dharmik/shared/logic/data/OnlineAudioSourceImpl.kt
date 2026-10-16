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

import com.shub39.dharmik.shared.core.domain.AudioSource
import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.Audios
import com.shub39.dharmik.shared.core.domain.slokaNumbers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

/**
 * Resolves the three audio tracks of every verse, preferring the locally stored file (local-first)
 * and otherwise the remote URL on the legacy Gita Supersite host.
 */
@Single(binds = [AudioSource::class])
class LocalFirstAudioSourceImpl(private val store: AudioStore) : AudioSource {
    override suspend fun getAudios(index: Int): List<Audios> =
        withContext(Dispatchers.Default) {
            val chapter = index.toLong()
            val slokaCount = slokaNumbers[index - 1]

            (1..slokaCount).map { i ->
                val verse = i.toLong()
                Audios(
                    moolSloka = localOrRemote(chapter, verse, AudioVariant.MOOL),
                    englishTranslation = localOrRemote(chapter, verse, AudioVariant.ENGLISH),
                    hindiTranslation = localOrRemote(chapter, verse, AudioVariant.HINDI),
                )
            }
        }

    private fun localOrRemote(chapter: Long, verse: Long, variant: AudioVariant): String =
        store.localFile(chapter, verse, variant)?.let { store.fileUri(it) }
            ?: GitaAudioUrls.url(chapter, verse, variant)
}
