package com.shub39.dharmik.bhagvad_gita.presentation.home

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.domain.VerseCardState

sealed interface HomeAction {
    data class SetFontSize(val fontSize: Float): HomeAction
    data class ChapterChange(val index: Int): HomeAction
    data class OnSetVerseCardState(val state: VerseCardState): HomeAction
    data class OnSetAppTheme(val appTheme: AppTheme): HomeAction
    data class SetFave(val verse: GitaVerse): HomeAction
    data object LoadBookMark: HomeAction
    data class LoadVerse(val verse: GitaVerse): HomeAction
}