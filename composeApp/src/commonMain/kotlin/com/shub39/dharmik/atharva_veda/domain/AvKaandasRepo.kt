package com.shub39.dharmik.atharva_veda.domain

interface AvKaandasRepo {
    suspend fun getKaandas(): Map<Int, List<AvVerse>>
}