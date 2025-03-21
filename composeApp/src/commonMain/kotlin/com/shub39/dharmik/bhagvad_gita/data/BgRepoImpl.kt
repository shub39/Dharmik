package com.shub39.dharmik.bhagvad_gita.data

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
class BgRepoImpl(
    private val bgDao: BgDao
) : BgRepo {
    override suspend fun getChapter(index: Int): GitaFile = withContext(Dispatchers.IO) {
        val decoder = Json {
            ignoreUnknownKeys = true
        }

        val jsonFile = async { Res.readBytes("files/bhagvad_gita/bhagavad_gita_chapter_$index.json").decodeToString() }.await()
        val file: GitaFile = decoder.decodeFromString(jsonFile)

        return@withContext file
    }

    override suspend fun getAudios(index: Int): List<Audios> = withContext(Dispatchers.IO) {
        val basePath = "files/gita_audio/CHAP${"%02d".format(index)}/"
        val slokaCount = slokaNumbers[index - 1]

        val audios = (1..slokaCount).map { slokaIndex ->
            Audios(
                moolSloka = Res.getUri("$basePath${"%02d".format(slokaIndex)}-mool_sloka.ogg"),
                englishTranslation = Res.getUri("$basePath${"%02d".format(slokaIndex)}-english_translations.ogg"),
                hindiTranslation = Res.getUri("$basePath${"%02d".format(slokaIndex)}-hindi_translation.ogg")
            )
        }

        return@withContext audios
    }

    override fun getFavesFlow(): Flow<List<GitaVerse>> {
        return bgDao.getFaves().map { flow ->
            flow.map { it.toGitaVerse() }
        }
    }

    override suspend fun setFave(verse: GitaVerse) {
        bgDao.setFave(verse.toBgVerseEntity())
    }

    override suspend fun deleteFave(verse: GitaVerse) {
        bgDao.deleteFave(verse.text)
    }

    companion object {
        private val slokaNumbers: List<Int> = listOf(
            47, 72, 43, 42, 29, 47, 30, 28, 34, 42, 55, 20, 35, 27, 20, 24, 28, 78
        )
    }
}