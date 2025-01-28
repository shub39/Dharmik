package com.shub39.dharmik.bhagvad_gita.presentation

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse

sealed interface BgAction {
    data class ChapterChange(val index: Int): BgAction
    data class SetFave(val verse: GitaVerse): BgAction
}