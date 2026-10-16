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
package com.shub39.dharmik.shared.ui.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.navigation.HomeRoutes
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.home.components.ChaptersSection
import com.shub39.dharmik.shared.ui.presentation.home.components.HomeSection
import com.shub39.dharmik.shared.ui.presentation.home.components.SettingsSection
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.app_name
import dharmik.shared.ui.generated.resources.book
import dharmik.shared.ui.generated.resources.buymeacoffee
import dharmik.shared.ui.generated.resources.chapters
import dharmik.shared.ui.generated.resources.github
import dharmik.shared.ui.generated.resources.home
import dharmik.shared.ui.generated.resources.settings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Home(onNavigateToVerses: () -> Unit, homeState: HomeState, onAction: (HomeAction) -> Unit) =
    PageFill {
        var currentDest: HomeRoutes by remember { mutableStateOf(HomeRoutes.HomeSection) }

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                listOf(
                        HomeRoutes.HomeSection,
                        HomeRoutes.ChaptersSection,
                        HomeRoutes.SettingsSection,
                    )
                    .forEach { route ->
                        item(
                            selected = currentDest == route,
                            onClick = { currentDest = route },
                            icon = {
                                Icon(
                                    painter = painterResource(route.iconRes()),
                                    contentDescription = "Sections",
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            alwaysShowLabel = false,
                            label = { Text(text = stringResource(route.labelRes())) },
                        )
                    }
            }
        ) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeFlexibleTopAppBar(
                        title = { Text(stringResource(Res.string.app_name)) },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                scrolledContainerColor = MaterialTheme.colorScheme.background
                            ),
                        actions = {
                            val uriHandler = LocalUriHandler.current

                            IconButton(
                                onClick = {
                                    uriHandler.openUri("https://github.com/shub39/Dharmik")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.github),
                                    contentDescription = "Github",
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            IconButton(
                                onClick = { uriHandler.openUri("https://buymeacoffee.com/shub39") }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.buymeacoffee),
                                    contentDescription = "Buy me a coffee",
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
            ) { padding ->
                AnimatedContent(
                    targetState = currentDest,
                    modifier = Modifier.padding(padding),
                    transitionSpec = {
                        (slideInVertically(tween(300), initialOffsetY = { it / 2 }) +
                                fadeIn(tween(300)))
                            .togetherWith(fadeOut(tween(300)))
                    },
                    label = "homeSections",
                ) { dest ->
                    when (dest) {
                        HomeRoutes.HomeSection ->
                            HomeSection(
                                onNavigateToVerses = onNavigateToVerses,
                                homeState = homeState,
                                onAction = onAction,
                            )

                        HomeRoutes.ChaptersSection ->
                            ChaptersSection(
                                onNavigateToVerses = onNavigateToVerses,
                                state = homeState,
                                onAction = onAction,
                            )

                        HomeRoutes.SettingsSection ->
                            SettingsSection(state = homeState, onAction = onAction)
                    }
                }
            }
        }
    }

private fun HomeRoutes.iconRes(): DrawableResource =
    when (this) {
        HomeRoutes.HomeSection -> Res.drawable.home
        HomeRoutes.ChaptersSection -> Res.drawable.book
        HomeRoutes.SettingsSection -> Res.drawable.settings
    }

private fun HomeRoutes.labelRes(): StringResource =
    when (this) {
        HomeRoutes.HomeSection -> Res.string.home
        HomeRoutes.ChaptersSection -> Res.string.chapters
        HomeRoutes.SettingsSection -> Res.string.settings
    }
