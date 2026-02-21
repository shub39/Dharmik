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
package com.shub39.dharmik.bhagvad_gita.data

import com.shub39.dharmik.bhagvad_gita.domain.AudioSource
import com.shub39.dharmik.bhagvad_gita.domain.Audios
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.domain.GitaFile
import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import dharmik.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class BgRepoImpl(private val bgDao: BgDao, private val audioSource: AudioSource) : BgRepo {
    override suspend fun getChapter(index: Int): GitaFile =
        withContext(Dispatchers.Default) {
            val decoder = Json { ignoreUnknownKeys = true }

            val jsonFile =
                async {
                        Res.readBytes("files/bhagvad_gita/bhagavad_gita_chapter_$index.json")
                            .decodeToString()
                    }
                    .await()
            val file: GitaFile = decoder.decodeFromString(jsonFile)

            return@withContext file
        }

    override suspend fun getAudios(index: Int): List<Audios> = audioSource.getAudios(index)

    override fun getFavesFlow(): Flow<List<GitaVerse>> {
        return bgDao.getFaves().map { flow -> flow.map { it.toGitaVerse() } }
    }

    override suspend fun setFave(verse: GitaVerse) {
        bgDao.setFave(verse.toBgVerseEntity())
    }

    override suspend fun deleteFave(verse: GitaVerse) {
        bgDao.deleteFave(verse.text)
    }
}
