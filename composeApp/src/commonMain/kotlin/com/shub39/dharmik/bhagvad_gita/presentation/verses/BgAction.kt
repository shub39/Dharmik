package com.shub39.dharmik.bhagvad_gita.presentation.verses

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.LongPair

sealed interface BgAction {
    data class ChapterChange(val index: Int): BgAction
    data class SetFave(val verse: GitaVerse): BgAction
    data class SetIndex(val mark: LongPair): BgAction
    data class SetVerses(val verses: List<GitaVerse>): BgAction
    data object LoadBookMark: BgAction
}