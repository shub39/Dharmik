package com.shub39.dharmik.atharva_veda.domain

import kotlinx.serialization.Serializable

@Serializable
data class AvVerse(
    val veda: String,
    val samhita: String,
    val kaanda: Int,
    val sukta: Int,
    val text: String
)