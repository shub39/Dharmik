package com.shub39.dharmik.atharva_veda.presentation

import com.shub39.dharmik.atharva_veda.domain.AvVerse
import com.shub39.dharmik.core.domain.IntPair

sealed interface AvAction {
    data class SetKaandas(val kaandas: List<AvVerse>): AvAction
    data class SetFave(val verse: AvVerse): AvAction
    data class SetBookMark(val mark: IntPair): AvAction
    data object LoadBookMark: AvAction
}