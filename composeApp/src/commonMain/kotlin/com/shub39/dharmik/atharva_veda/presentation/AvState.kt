package com.shub39.dharmik.atharva_veda.presentation

import com.shub39.dharmik.atharva_veda.domain.AvVerse

data class AvState (
    val kaandas: Map<Int, List<AvVerse>> = emptyMap(),
    val currentKaandas: List<AvVerse> = emptyList(),
    val favorites: List<AvVerse> = emptyList()
)