package com.shub39.dharmik.bhagvad_gita.data

import com.shub39.dharmik.bhagvad_gita.domain.AudioSource
import com.shub39.dharmik.bhagvad_gita.domain.Audios
import com.shub39.dharmik.bhagvad_gita.domain.slokaNumbers

class OnlineAudioSourceImpl: AudioSource {
    override suspend fun getAudios(index: Int): List<Audios> {
        val baseUrl = "https://www.gitasupersite.iitk.ac.in/sites/default/files/audio"
        val slokaCount = slokaNumbers[index - 1]

        val audios = (1..slokaCount).map { slokaIndex ->
            val slokaStr = "%02d".format(slokaIndex)
            Audios(
                moolSloka = "$baseUrl/CHAP$index/$index-$slokaIndex.MP3",
                englishTranslation = "$baseUrl/Purohit/$index.$slokaIndex.mp3",
                hindiTranslation = "$baseUrl/Tejomayananda/chapter/C$index-H-$slokaStr.mp3"
            )
        }

        return audios
    }
}