package com.shub39.dharmik.bhagvad_gita.presentation.verses

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.VerseCardState
import com.shub39.dharmik.core.domain.LongPair

sealed interface VersesAction {
    data class SetFave(val verse: GitaVerse): VersesAction
    data class SetIndex(val mark: LongPair): VersesAction
    data class SetVerseCardState(val state: VerseCardState): VersesAction
}