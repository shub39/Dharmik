package com.shub39.dharmik.bhagvad_gita.data

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

class BgRepoImpl(
    private val bgDao: BgDao
) : BgRepo {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getChapter(index: Int): GitaFile = withContext(Dispatchers.IO) {
        val decoder = Json {
            ignoreUnknownKeys = true
        }

        val jsonFile = async { Res.readBytes(bgFileName(index)).decodeToString() }.await()
        val file: GitaFile = decoder.decodeFromString(jsonFile)

        return@withContext file
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
        private fun bgFileName(index: Int) =
            "files/bhagvad_gita/bhagavad_gita_chapter_$index.json"
    }
}