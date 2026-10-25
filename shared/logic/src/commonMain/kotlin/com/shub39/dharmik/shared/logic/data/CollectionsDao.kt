/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.logic.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao {
    @Transaction
    @Query("SELECT * FROM collections ORDER BY position ASC")
    fun getCollections(): Flow<List<CollectionWithVerses>>

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :id")
    fun getCollection(id: Long): Flow<CollectionWithVerses?>

    @Query("SELECT COALESCE(MAX(position), -1) FROM collections")
    suspend fun maxCollectionPosition(): Int

    @Insert suspend fun insertCollection(collection: CollectionsEntity): Long

    @Query("UPDATE collections SET name = :name, description = :description WHERE id = :id")
    suspend fun updateCollection(id: Long, name: String, description: String)

    @Query("DELETE FROM collections WHERE id = :id") suspend fun deleteCollection(id: Long)

    @Query(
        "SELECT COALESCE(MAX(position), -1) FROM collection_verses WHERE collectionId = :collectionId"
    )
    suspend fun maxVersePosition(collectionId: Long): Int

    @Insert suspend fun insertVerse(verse: VerseCollectionEntity): Long

    @Query("UPDATE collection_verses SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("DELETE FROM collection_verses WHERE id = :id") suspend fun deleteVerse(id: Long)
}
