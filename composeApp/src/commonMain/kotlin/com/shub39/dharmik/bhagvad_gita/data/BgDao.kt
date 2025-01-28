package com.shub39.dharmik.bhagvad_gita.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BgDao {
    @Query("SELECT * FROM bhagvad_gita_favorites")
    fun getFaves(): Flow<List<BgVerseEntity>>

    @Upsert
    suspend fun setFave(verse: BgVerseEntity)

    @Query("DELETE FROM bhagvad_gita_favorites WHERE text = :text")
    suspend fun deleteFave(text: String)
}