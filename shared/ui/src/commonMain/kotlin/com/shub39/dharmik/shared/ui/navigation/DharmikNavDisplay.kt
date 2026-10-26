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
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.ui.presentation.chapters.Chapters
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionDetail as CollectionDetailScreen
import com.shub39.dharmik.shared.ui.presentation.collections.Collections as CollectionsScreen
import com.shub39.dharmik.shared.ui.presentation.collections.add.AddToCollection as AddToCollectionScreen
import com.shub39.dharmik.shared.ui.presentation.home.Home
import com.shub39.dharmik.shared.ui.presentation.player.Player as PlayerScreen
import com.shub39.dharmik.shared.ui.presentation.settings.About as AboutScreen
import com.shub39.dharmik.shared.ui.presentation.settings.Changelog as ChangelogScreen
import com.shub39.dharmik.shared.ui.presentation.settings.Settings
import com.shub39.dharmik.shared.ui.presentation.verses.Verses
import com.shub39.dharmik.shared.ui.presentation.viewmodels.AddToCollectionViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.ChaptersViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.CollectionDetailViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.CollectionsViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.HomeViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.MainViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.PlayerViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.SettingsViewModel
import com.shub39.dharmik.shared.ui.presentation.viewmodels.VersesViewModel
import com.shub39.dharmik.shared.ui.theme.DharmikTheme
import com.shub39.dharmik.shared.ui.util.LocalWindowSizeClass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DharmikNavDisplay(
    modifier: Modifier = Modifier,
    paywall: @Composable (Boolean, () -> Unit) -> Unit,
) {
    val mainVM = koinViewModel<MainViewModel>()
    val state by mainVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    val stack = retain { TopLevelBackStack() }

    // Writing collections is a Plus feature: every mutating affordance of the collections screens
    // goes through this, which either performs the action or opens the paywall.
    val requestWrite: (() -> Unit) -> Unit = { action ->
        if (state.isUserSubscribed) action() else stack.add(Paywall)
    }

    // Listening to playlists is a Plus feature: the player destination and every play affordance
    // of the chapter and collection lists go through this.
    val requestPlay: (() -> Unit) -> Unit = { action ->
        if (state.isUserSubscribed) action() else stack.add(Paywall)
    }

    val collectVerse: (GitaVerse) -> Unit = { verse ->
        if (state.isUserSubscribed) stack.add(AddToCollection(verse.chapter, verse.verse))
        else stack.add(Paywall)
    }

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
                            chaptersScreens(stack, requestPlay)
                            collectionScreens(stack, requestWrite, requestPlay)
                            versesScreens(stack, collectVerse)
                            playerScreens(stack)
                            settingsScreens(stack)

                            entry<Paywall>(metadata = verticalTransitionMetadata()) {
                                LaunchedEffect(Unit) { mainVM.trackPaywallOpened() }

                                paywall(state.isUserSubscribed) {
                                    mainVM.updateSubscription()
                                    stack.removeLast()
                                }
                            }
                        },
                )
            }
        }
    }
}

private fun EntryProviderScope<Route>.collectionScreens(
    stack: TopLevelBackStack,
    requestWrite: (() -> Unit) -> Unit,
    requestPlay: (() -> Unit) -> Unit,
) {
    // The list pane of the `Collections` + `CollectionDetail` list-detail scene.
    entry<Collections>(metadata = ListDetailScene.listPane() + fadeTransitionMetadata()) {
        val viewModel = koinViewModel<CollectionsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val currentSelected = stack.backStack.lastOrNull() as? CollectionDetail

        CollectionsScreen(
            state = state,
            currentSelected = currentSelected?.id,
            onAction = viewModel::onAction,
            onNavigateToDetail = { stack.add(CollectionDetail(it)) },
            onPlayCollection = { id -> requestPlay { stack.add(Player(collectionId = id)) } },
            onRequestWrite = requestWrite,
            onRequestPlay = requestPlay,
        )
    }

    entry<CollectionDetail>(
        metadata = ListDetailScene.detailPane() + horizontalTransitionMetadata()
    ) { route ->
        val viewModel = koinViewModel<CollectionDetailViewModel> { parametersOf(route) }
        val state by viewModel.state.collectAsStateWithLifecycle()

        CollectionDetailScreen(
            state = state,
            onAction = viewModel::onAction,
            onNavigateToVerses = { chapter, verse ->
                stack.add(Verses(chapter = chapter, verse = verse, saveBookmarks = false))
            },
            onBack = { stack.removeLast() },
            onRequestWrite = requestWrite,
        )
    }

    // Two-pane capable so that the reader stays visible on wide windows.
    entry<AddToCollection>(metadata = TwoPaneScene.twoPane() + verticalTransitionMetadata()) { route
        ->
        val viewModel = koinViewModel<AddToCollectionViewModel> { parametersOf(route) }
        val state by viewModel.state.collectAsStateWithLifecycle()

        AddToCollectionScreen(
            state = state,
            onAction = viewModel::onAction,
            onBack = { stack.removeLast() },
        )
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

private fun EntryProviderScope<Route>.chaptersScreens(
    stack: TopLevelBackStack,
    requestPlay: (() -> Unit) -> Unit,
) {
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
            onPlayChapter = { chapter -> requestPlay { stack.add(Player(chapter = chapter)) } },
            onRequestPlay = requestPlay,
        )
    }
}

private fun EntryProviderScope<Route>.playerScreens(stack: TopLevelBackStack) {
    // Deliberately not a list-detail or two-pane entry: the player owns the whole window. The Plus
    // gate lives on the screen itself, which offers the paywall instead of the transport controls.
    entry<Player>(metadata = fadeTransitionMetadata()) { route ->
        val viewModel = koinViewModel<PlayerViewModel> { parametersOf(route) }
        val state by viewModel.state.collectAsStateWithLifecycle()

        PlayerScreen(
            state = state,
            onAction = viewModel::onAction,
            onNavigateToPaywall = { stack.add(Paywall) },
        )
    }
}

private fun EntryProviderScope<Route>.versesScreens(
    stack: TopLevelBackStack,
    onAddToCollection: (GitaVerse) -> Unit,
) {
    entry<Verses>(
        metadata =
            TwoPaneScene.twoPane() + ListDetailScene.detailPane() + verticalTransitionMetadata()
    ) { route ->
        val viewModel = koinViewModel<VersesViewModel> { parametersOf(route) }
        val state by viewModel.state.collectAsStateWithLifecycle()

        Verses(
            state = state,
            action = viewModel::onAction,
            onBack = { stack.removeLast() },
            onAddToCollection = onAddToCollection,
        )
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
            onNavigateToPaywall = { stack.add(Paywall) },
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
