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
package com.shub39.dharmik.bhagvad_gita.presentation.viewmodels

import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.bhagvad_gita.presentation.verses.VersesState
import kotlinx.coroutines.flow.MutableStateFlow

class StateLayer {
    val homeState = MutableStateFlow(HomeState())
    val versesState = MutableStateFlow(VersesState())
}
