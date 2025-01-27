package com.shub39.dharmik.atharva_veda.domain

import kotlinx.coroutines.flow.Flow

interface AvKaandasRepo {
    suspend fun getKaandas(): Map<Int, List<AvVerse>>

    fun getFavesFlow(): Flow<List<AvVerse>>

    suspend fun setFave(verse: AvVerse)

    suspend fun deleteFave(verse: AvVerse)
}