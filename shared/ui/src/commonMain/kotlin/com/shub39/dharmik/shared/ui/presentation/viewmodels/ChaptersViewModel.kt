/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.ui.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.ui.presentation.chapters.ChaptersAction
import com.shub39.dharmik.shared.ui.presentation.chapters.ChaptersState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ChaptersViewModel(@Provided private val analytics: AnalyticsWrapper) : ViewModel() {
    private val _state = MutableStateFlow(ChaptersState())
    val state = _state.asStateFlow()

    init {
        analytics.trackEvent(AnalyticsEvent.CHAPTERS_VIEWED, emptyMap())
    }

    /**
     * [ChaptersAction.ChapterChange] deliberately performs no work here. The picked chapter is the
     * navigation argument of `Verses`, so the screen turns the action into navigation instead of
     * pushing the selection into shared state.
     */
    fun onAction(action: ChaptersAction) {
        when (action) {
            is ChaptersAction.ChapterChange ->
                analytics.trackEvent(
                    AnalyticsEvent.CHAPTER_OPENED,
                    mapOf("chapter" to action.index),
                )
        }
    }
}
