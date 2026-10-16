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
package com.shub39.dharmik.shared.ui.app

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.dharmik.shared.ui.navigation.Routes
import com.shub39.dharmik.shared.ui.presentation.home.Home
import com.shub39.dharmik.shared.ui.presentation.verses.Verses
import com.shub39.dharmik.shared.ui.presentation.viewmodels.HomeViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.VersesViewModel
import com.shub39.dharmik.shared.ui.theme.DharmikTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    homeViewModel: HomeViewModel = koinViewModel(),
    versesViewModel: VersesViewModel = koinViewModel(),
) {
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val versesState by versesViewModel.state.collectAsStateWithLifecycle()

    DharmikTheme(theme = homeState.theme) {
        val backStack = rememberNavBackStack(Routes.configuration, Routes.Home)

        NavDisplay(
            backStack = backStack,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            onBack = { backStack.removeLastOrNull() },
            entryProvider =
                entryProvider {
                    entry<Routes.Home> {
                        Home(
                            onNavigateToVerses = { backStack.add(Routes.Verses) },
                            homeState = homeState,
                            onAction = homeViewModel::onAction,
                        )
                    }

                    entry<Routes.Verses> {
                        Verses(
                            state = versesState,
                            action = versesViewModel::onAction,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                },
        )
    }
}
