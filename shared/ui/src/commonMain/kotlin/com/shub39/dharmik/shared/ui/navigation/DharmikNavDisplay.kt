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
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.shub39.dharmik.shared.ui.presentation.chapters.Chapters
import com.shub39.dharmik.shared.ui.presentation.home.Home
import com.shub39.dharmik.shared.ui.presentation.settings.About as AboutScreen
import com.shub39.dharmik.shared.ui.presentation.settings.Changelog as ChangelogScreen
import com.shub39.dharmik.shared.ui.presentation.settings.Settings
import com.shub39.dharmik.shared.ui.presentation.verses.Verses
import com.shub39.dharmik.shared.ui.presentation.viewmodels.ChaptersViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.HomeViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.MainViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.SettingsViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.VersesViewModel
import com.shub39.dharmik.shared.ui.theme.DharmikTheme
import com.shub39.dharmik.shared.ui.util.LocalWindowSizeClass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DharmikNavDisplay(modifier: Modifier = Modifier) {
    val mainVM = koinViewModel<MainViewModel>()
    val state by mainVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    val stack = retain { TopLevelBackStack() }

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        DharmikTheme(theme = state.theme) {
            SharedTransitionLayout(modifier = modifier.background(colorScheme.background)) {
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
                    entryDecorators =
                        listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                    entryProvider =
                        entryProvider {
                            homeScreens(stack)
                            chaptersScreens(stack)
                            versesScreens(stack)
                            settingsScreens(stack)
                        },
                )
            }
        }
    }
}

private fun EntryProviderScope<Route>.homeScreens(stack: TopLevelBackStack) {
    // Two-pane capable so that opening a bookmark or favourite keeps the list visible on wide
    // windows.
    entry<Home>(metadata = TwoPaneScene.twoPane() + fadeTransitionMetadata()) {
        val viewModel = koinViewModel<HomeViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Home(
            state = state,
            onNavigateToSettings = { stack.add(Settings) },
            onNavigateToVerses = { stack.add(it) },
        )
    }
}

private fun EntryProviderScope<Route>.chaptersScreens(stack: TopLevelBackStack) {
    // The list pane of the `Chapters` + `Verses` list-detail scene.
    entry<Chapters>(metadata = ListDetailScene.listPane() + fadeTransitionMetadata()) {
        val viewModel = koinViewModel<ChaptersViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val currentSelected = stack.backStack.lastOrNull() as? Verses

        Chapters(
            state = state,
            onAction = viewModel::onAction,
            currentSelected = currentSelected?.chapter?.toInt()?.minus(1),
            onNavigateToVerses = { stack.add(Verses(chapter = it)) },
        )
    }
}

private fun EntryProviderScope<Route>.versesScreens(stack: TopLevelBackStack) {
    entry<Verses>(
        metadata =
            TwoPaneScene.twoPane() + ListDetailScene.detailPane() + verticalTransitionMetadata()
    ) { route ->
        val viewModel = koinViewModel<VersesViewModel> { parametersOf(route) }
        val state by viewModel.state.collectAsStateWithLifecycle()

        Verses(state = state, action = viewModel::onAction, onBack = { stack.removeLast() })
    }
}

private fun EntryProviderScope<Route>.settingsScreens(stack: TopLevelBackStack) {
    entry<Settings>(metadata = TwoPaneScene.twoPane() + verticalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Settings(
            state = state,
            onAction = viewModel::onAction,
            onNavigateToAbout = { stack.add(About) },
            onNavigateToChangelog = { stack.add(Changelog) },
            onNavigateBack = { stack.removeLast() },
        )
    }

    entry<About>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.trackAboutViewed() }

        AboutScreen(versionName = state.currentVersion ?: "1.0.0", onBack = { stack.removeLast() })
    }

    entry<Changelog>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.trackChangelogViewed() }

        ChangelogScreen(changelog = state.changelog, onBack = { stack.removeLast() })
    }
}
