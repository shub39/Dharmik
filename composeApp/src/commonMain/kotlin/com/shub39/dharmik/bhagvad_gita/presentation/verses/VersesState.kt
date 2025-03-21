package com.shub39.dharmik.bhagvad_gita.presentation.verses

import androidx.compose.foundation.pager.PagerState
import com.shub39.dharmik.bhagvad_gita.domain.Audios
import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse

data class VersesState(
    val currentVerses: List<GitaVerse> = emptyList(),
    val audioFiles: List<Audios> = emptyList(),
    val saveBookMarks: Boolean = true,
    val favorites: List<GitaVerse> = emptyList(),
    val pagerState: PagerState = PagerState { 0 }
)