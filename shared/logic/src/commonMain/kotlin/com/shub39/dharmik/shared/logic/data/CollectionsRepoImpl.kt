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

import com.shub39.dharmik.shared.core.domain.Collections
import com.shub39.dharmik.shared.core.domain.CollectionsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [CollectionsRepo::class])
class CollectionsRepoImpl(private val dao: CollectionsDao) : CollectionsRepo {

    override fun getCollectionsFlow(): Flow<List<Collections>> {
        return dao.getCollections().map { collections -> collections.map { it.toCollections() } }
    }

    override fun getCollectionFlow(id: Long): Flow<Collections?> {
        return dao.getCollection(id).map { it?.toCollections() }
    }

    override suspend fun createCollection(name: String, description: String): Long {
        return dao.insertCollection(
            CollectionsEntity(
                name = name.trim(),
                description = description.trim(),
                position = dao.maxCollectionPosition() + 1,
            )
        )
    }

    override suspend fun updateCollection(id: Long, name: String, description: String) {
        dao.updateCollection(id, name.trim(), description.trim())
    }

    override suspend fun deleteCollection(id: Long) {
        dao.deleteCollection(id)
    }

    override suspend fun addVerse(
        collectionId: Long,
        chapter: Long,
        verse: Long,
        notes: String,
    ): Long {
        return dao.insertVerse(
            VerseCollectionEntity(
                collectionId = collectionId,
                position = dao.maxVersePosition(collectionId) + 1,
                chapter = chapter,
                verse = verse,
                notes = notes.trim(),
            )
        )
    }

    override suspend fun updateVerseNotes(verseCollectionId: Long, notes: String) {
        dao.updateNotes(verseCollectionId, notes.trim())
    }

    override suspend fun removeVerse(verseCollectionId: Long) {
        dao.deleteVerse(verseCollectionId)
    }
}
