package com.shub39.dharmik.atharva_veda.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AvDao {
    @Upsert
    suspend fun upsertAvVerse(avEntity: AvEntity)

    @Delete
    suspend fun deleteAvVerse(avEntity: AvEntity)

    @Query("SELECT EXISTS(SELECT * FROM atharva_veda_favorites WHERE text = :verse)")
    suspend fun isVerseFaved(verse: String): Boolean

    @Query("SELECT * FROM atharva_veda_favorites")
    fun getAvVerses(): Flow<List<AvEntity>>
}