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
package com.shub39.dharmik.bhagvad_gita.data

import androidx.room.TypeConverter
import com.shub39.dharmik.bhagvad_gita.domain.Commentaries
import com.shub39.dharmik.bhagvad_gita.domain.Translations
import kotlinx.serialization.json.Json

object BgVerseTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromCommentaries(commentaries: Commentaries): String {
        return json.encodeToString(commentaries)
    }

    @TypeConverter
    fun toCommentaries(commentariesJson: String): Commentaries {
        return json.decodeFromString(commentariesJson)
    }

    @TypeConverter
    fun fromTranslations(translations: Translations): String {
        return json.encodeToString(translations)
    }

    @TypeConverter
    fun toTranslations(translationsJson: String): Translations {
        return json.decodeFromString(translationsJson)
    }
}
