package com.shub39.dharmik.bhagvad_gita.presentation.home

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.AppTheme

sealed interface HomeAction {
    data class ChapterChange(val index: Int): HomeAction
    data class OnSetAppTheme(val appTheme: AppTheme): HomeAction
    data class SetFave(val verse: GitaVerse): HomeAction
    data class SetVerses(val verses: List<GitaVerse>): HomeAction
    data object LoadBookMark: HomeAction
    data class LoadVerse(val verse: GitaVerse): HomeAction
}