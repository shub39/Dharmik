package com.shub39.dharmik.atharva_veda.data

import androidx.room.TypeConverter
import com.shub39.dharmik.atharva_veda.domain.AvVerse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AvConverters {
    @TypeConverter
    fun fromAvVerse(value: AvVerse): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toAvVerse(value: String): AvVerse {
        return Json.decodeFromString(value)
    }
}