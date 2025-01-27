package com.shub39.dharmik.atharva_veda.data

import com.shub39.dharmik.atharva_veda.domain.AvKaandasRepo
import com.shub39.dharmik.atharva_veda.domain.AvVerse
import dharmik.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class AvKaandasRepoImpl(
    private val avDao: AvDao
) : AvKaandasRepo {

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getKaandas(): Map<Int, List<AvVerse>> = withContext(Dispatchers.IO) {
        val decoder = Json {
            ignoreUnknownKeys = true
        }

        var kaandas = mapOf<Int, List<AvVerse>>()

        for (kaanda in 1..KAANDAS_COUNT) {
            val file = async { Res.readBytes(kaandasFileName(kaanda)).decodeToString() }.await()
            val verses: List<AvVerse> = decoder.decodeFromString(file)
            kaandas = kaandas.plus(kaanda to verses)
        }

        return@withContext kaandas
    }

    override fun getFavesFlow(): Flow<List<AvVerse>> {
        return avDao.getAvVerses().map { flow ->
            flow.map { it.toAvVerse() }
        }
    }

    override suspend fun setOrUnsetFave(verse: AvVerse) {
        val entity = verse.toAvEntity()
        val isFave = avDao.isVerseFaved(entity.text)

        if (isFave) {
            avDao.deleteAvVerse(entity)
        } else {
            avDao.upsertAvVerse(entity)
        }
    }

    companion object {
        private fun kaandasFileName(kaanda: Int) = "files/atharva_veda/atharvaveda_kaanda_$kaanda.json"
        private const val KAANDAS_COUNT = 20
    }
}