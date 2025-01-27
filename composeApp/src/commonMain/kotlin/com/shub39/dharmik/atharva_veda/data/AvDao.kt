package com.shub39.dharmik.atharva_veda.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AvDao {
    @Upsert
    suspend fun upsertAvVerse(avEntity: AvEntity)

    @Query("DELETE FROM atharva_veda_favorites WHERE text = :text")
    suspend fun deleteAvVerse(text: String)

    @Query("SELECT * FROM atharva_veda_favorites")
    fun getAvVerses(): Flow<List<AvEntity>>
}