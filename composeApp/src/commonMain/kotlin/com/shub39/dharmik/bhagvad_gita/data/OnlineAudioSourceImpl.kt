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
import com.shub39.dharmik.bhagvad_gita.domain.slokaNumbers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnlineAudioSourceImpl : AudioSource {
    override suspend fun getAudios(index: Int): List<Audios> =
        withContext(Dispatchers.Default) {
            val baseUrl = "https://www.gitasupersite.iitk.ac.in/sites/default/files/audio"
            val slokaCount = slokaNumbers[index - 1]

            val audios =
                (1..slokaCount).map { slokaIndex ->
                    val slokaStr = "%02d".format(slokaIndex)
                    Audios(
                        moolSloka = "$baseUrl/CHAP$index/$index-$slokaIndex.MP3",
                        englishTranslation = "$baseUrl/Purohit/$index.$slokaIndex.mp3",
                        hindiTranslation = "$baseUrl/Tejomayananda/chapter/C$index-H-$slokaStr.mp3",
                    )
                }

            return@withContext audios
        }
}
