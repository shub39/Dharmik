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
package com.shub39.dharmik.shared.core.domain

import kotlinx.coroutines.flow.Flow

/**
 * Stores user created collections of verses and the notes attached to each collected verse.
 *
 * Collections and their verses are ordered by [Collections.index] and [VerseCollection.index]; new
 * entries are always appended at the end, so writers never have to reason about the whole list.
 */
interface CollectionsRepo {
    fun getCollectionsFlow(): Flow<List<Collections>>

    fun getCollectionFlow(id: Long): Flow<Collections?>

    /** Creates a collection and returns its id. */
    suspend fun createCollection(name: String, description: String = ""): Long

    suspend fun updateCollection(id: Long, name: String, description: String)

    suspend fun deleteCollection(id: Long)

    /** Adds [verse] of [chapter] to [collectionId] and returns the new entry's id. */
    suspend fun addVerse(collectionId: Long, chapter: Long, verse: Long, notes: String = ""): Long

    suspend fun updateVerseNotes(verseCollectionId: Long, notes: String)

    suspend fun removeVerse(verseCollectionId: Long)
}
