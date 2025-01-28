package com.shub39.dharmik.bhagvad_gita.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shub39.dharmik.bhagvad_gita.domain.Commentaries
import com.shub39.dharmik.bhagvad_gita.domain.Translations

@Entity(tableName = "bhagvad_gita_favorites")
data class BgVerseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapter: Long,
    val verse: Long,
    val text: String,
    val commentaries: Commentaries,
    val translations: Translations
)