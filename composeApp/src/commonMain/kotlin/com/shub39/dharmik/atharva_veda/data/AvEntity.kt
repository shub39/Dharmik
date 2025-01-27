package com.shub39.dharmik.atharva_veda.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atharva_veda_favorites")
class AvEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val veda: String,
    val samhita: String,
    val kaanda: Int,
    val sukta: Int,
    val text: String
)