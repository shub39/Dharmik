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
package com.shub39.dharmik.bhagvad_gita.presentation.home

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.domain.VerseCardState

sealed interface HomeAction {
    data class SetFontSize(val fontSize: Float) : HomeAction

    data class ChapterChange(val index: Int) : HomeAction

    data class OnSetVerseCardState(val state: VerseCardState) : HomeAction

    data class OnSetAppTheme(val appTheme: AppTheme) : HomeAction

    data class SetFave(val verse: GitaVerse) : HomeAction

    data object LoadBookMark : HomeAction

    data class LoadVerse(val verse: GitaVerse) : HomeAction
}
