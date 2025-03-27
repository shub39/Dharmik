package com.shub39.dharmik.bhagvad_gita.presentation.verses

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.VerseCardState
import com.shub39.dharmik.core.domain.LongPair
import kotlinx.coroutines.CoroutineScope

sealed interface VersesAction {
    data class SetFave(val verse: GitaVerse): VersesAction
    data class SetIndex(val mark: LongPair): VersesAction
    data class SetAutoPlay(val autoPlay: Boolean): VersesAction
    data class ChangeVerse(val index: Int, val coroutineScope: CoroutineScope): VersesAction
    data class Play(val index: Int): VersesAction
    data object Pause: VersesAction
    data class SetVerseCardState(val state: VerseCardState): VersesAction
}