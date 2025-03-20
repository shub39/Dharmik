package com.shub39.dharmik.bhagvad_gita.presentation.home

import com.shub39.dharmik.core.domain.LongPair
import com.shub39.dharmik.core.presentation.theme.Theme

data class HomeState(
    val theme: Theme = Theme(),
    val chapters: Int = 18,
    val currentBookMark: LongPair = LongPair(0, 0),
)