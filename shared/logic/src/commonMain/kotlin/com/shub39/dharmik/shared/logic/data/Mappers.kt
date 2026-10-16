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

import com.shub39.dharmik.shared.core.domain.GitaVerse
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun BgVerseEntity.toGitaVerse(): GitaVerse {
    return GitaVerse(
        chapter = chapter,
        verse = verse,
        text = text,
        commentaries = json.decodeFromString(commentaries),
        translations = json.decodeFromString(translations),
    )
}

fun GitaVerse.toBgVerseEntity(): BgVerseEntity {
    return BgVerseEntity(
        chapter = chapter,
        verse = verse,
        text = text,
        commentaries = json.encodeToString(commentaries),
        translations = json.encodeToString(translations),
    )
}
