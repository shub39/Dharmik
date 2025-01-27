package com.shub39.dharmik.atharva_veda.presentation

import com.shub39.dharmik.atharva_veda.domain.AvVerse

sealed interface AvAction {
    data class SetKaandas(val kaandas: List<AvVerse>): AvAction
}