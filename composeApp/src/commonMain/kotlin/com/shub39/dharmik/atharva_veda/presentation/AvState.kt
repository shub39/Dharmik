package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.pager.PagerState
import com.shub39.dharmik.atharva_veda.domain.AvVerse
import com.shub39.dharmik.core.domain.IntPair

data class AvState (
    val kaandas: Map<Int, List<AvVerse>> = emptyMap(),
    val currentKaandas: List<AvVerse> = emptyList(),
    val favorites: List<AvVerse> = emptyList(),
    val currentBookMark: IntPair = IntPair(0, 0),
    val pagerState: PagerState = PagerState { 0 }
)