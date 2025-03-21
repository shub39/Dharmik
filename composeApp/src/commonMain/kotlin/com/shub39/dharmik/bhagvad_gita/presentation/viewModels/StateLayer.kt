package com.shub39.dharmik.bhagvad_gita.presentation.viewModels

import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.bhagvad_gita.presentation.verses.VersesState
import kotlinx.coroutines.flow.MutableStateFlow

class StateLayer {
    val homeState = MutableStateFlow(HomeState())
    val versesState = MutableStateFlow(VersesState())
}