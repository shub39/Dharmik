package com.shub39.dharmik.bhagvad_gita.presentation

import androidx.compose.foundation.pager.PagerState
import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.LongPair

data class BgState(
    val chapters: Int = 18,
    val currentFile: List<GitaVerse> = emptyList(),
    val favorites: List<GitaVerse> = emptyList(),
    val currentBookMark: LongPair = LongPair(0, 0),
    val pagerState: PagerState = PagerState { 0 }
)
