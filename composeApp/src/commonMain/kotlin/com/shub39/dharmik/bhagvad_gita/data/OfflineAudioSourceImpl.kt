package com.shub39.dharmik.bhagvad_gita.data

import com.shub39.dharmik.bhagvad_gita.domain.AudioSource
import com.shub39.dharmik.bhagvad_gita.domain.Audios
import com.shub39.dharmik.bhagvad_gita.domain.slokaNumbers
import dharmik.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class OfflineAudioSourceImpl: AudioSource {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getAudios(index: Int): List<Audios> {
        val basePath = "files/gita_audio/CHAP$index/"
        val slokaCount = slokaNumbers[index - 1]

        val audios = (1..slokaCount).map { slokaIndex ->
            Audios(
                moolSloka = Res.getUri("$basePath${"%02d".format(slokaIndex)}-mool_sloka.ogg"),
                englishTranslation = Res.getUri("$basePath${"%02d".format(slokaIndex)}-english_translations.ogg"),
                hindiTranslation = Res.getUri("$basePath${"%02d".format(slokaIndex)}-hindi_translation.ogg")
            )
        }

        return audios
    }
}