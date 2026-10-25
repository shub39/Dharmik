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

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * One collected verse. Only the chapter and verse numbers are stored; the verse text itself is read
 * from the bundled chapter JSON so it always follows the user's selected language.
 *
 * Deleting a collection cascades to its verses.
 */
@Entity(
    tableName = "collection_verses",
    foreignKeys =
        [
            ForeignKey(
                entity = CollectionsEntity::class,
                parentColumns = ["id"],
                childColumns = ["collectionId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("collectionId")],
)
data class VerseCollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long,
    val position: Int,
    val chapter: Long,
    val verse: Long,
    val notes: String,
)
