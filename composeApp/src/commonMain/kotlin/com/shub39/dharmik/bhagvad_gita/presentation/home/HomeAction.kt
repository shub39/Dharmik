package com.shub39.dharmik.bhagvad_gita.presentation.home

import com.shub39.dharmik.core.domain.AppTheme

sealed interface HomeAction {
    data class OnSetAppTheme(val appTheme: AppTheme): HomeAction
}