package com.shub39.dharmik.bhagvad_gita.data

import androidx.room.TypeConverter
import com.shub39.dharmik.bhagvad_gita.domain.Commentaries
import com.shub39.dharmik.bhagvad_gita.domain.Translations
import kotlinx.serialization.encodeToString
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