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
import com.shub39.dharmik.shared.core.domain.LongPair
import com.shub39.dharmik.shared.core.domain.VerseCollection

/**
 * Room cannot order the rows of an `@Relation`, so the ordering key is applied here instead of in
 * the query.
 */
fun CollectionWithVerses.toCollections(): Collections {
    return Collections(
        id = collection.id,
        name = collection.name,
        description = collection.description,
        index = collection.position,
        verses = verses.sortedBy { it.position }.map { it.toVerseCollection() },
    )
}

fun VerseCollectionEntity.toVerseCollection(): VerseCollection {
    return VerseCollection(
        id = id,
        index = position,
        verse = LongPair(chapter, verse),
        notes = notes,
    )
}
