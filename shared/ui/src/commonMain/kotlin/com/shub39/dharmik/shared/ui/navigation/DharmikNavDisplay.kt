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
package com.shub39.dharmik.shared.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.shub39.dharmik.shared.ui.presentation.home.Home
import com.shub39.dharmik.shared.ui.presentation.verses.Verses
import com.shub39.dharmik.shared.ui.presentation.viewmodels.HomeViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.MainViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.VersesViewModel
import com.shub39.dharmik.shared.ui.theme.DharmikTheme
import com.shub39.dharmik.shared.ui.util.LocalWindowSizeClass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DharmikNavDisplay(modifier: Modifier = Modifier) {
    val mainVM = koinViewModel<MainViewModel>()
    val state by mainVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    val stack = retain { TopLevelBackStack() }

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        DharmikTheme(theme = state.theme) {
            SharedTransitionLayout(modifier = modifier) {
                val listDetailSceneStrategy =
                    remember(windowSizeClass) { ListDetailSceneStrategy<Route>(windowSizeClass) }
                val twoPaneSceneStrategy =
                    remember(windowSizeClass) { TwoPaneSceneStrategy<Route>(windowSizeClass) }
                val responsiveNavigationSceneDecoratorStrategy =
                    rememberResponsiveNavigationSceneDecoratorStrategy<Route>(
                        navBar = {
                            NavBar(
                                currentRoute = stack.backStack.lastOrNull(),
                                onNavigate = { stack.add(it) },
                            )
                        },
                        navRail = {
                            NavRail(
                                currentRoute = stack.backStack.lastOrNull(),
                                onNavigate = { stack.add(it) },
                            )
                        },
                        sharedTransitionScope = this,
                    )

                NavDisplay(
                    backStack = stack.backStack,
                    onBack = { stack.removeLast() },
                    sharedTransitionScope = this,
                    sceneStrategies = listOf(twoPaneSceneStrategy, listDetailSceneStrategy),
                    sceneDecoratorStrategies = listOf(responsiveNavigationSceneDecoratorStrategy),
                    entryProvider =
                        entryProvider {
                            entry<Home> {
                                val viewModel = koinViewModel<HomeViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                Home(
                                    onAction = viewModel::onAction,
                                    onNavigateToVerses = { stack.add(Verses) },
                                    state = state,
                                )
                            }

                            entry<Verses> {
                                val viewModel = koinViewModel<VersesViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                Verses(
                                    state = state,
                                    action = viewModel::onAction,
                                    onBack = { stack.removeLast() },
                                )
                            }

                            entry<Settings> {}
                        },
                )
            }
        }
    }
}
