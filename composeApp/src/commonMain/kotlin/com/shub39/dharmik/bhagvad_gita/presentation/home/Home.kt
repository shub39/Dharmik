package com.shub39.dharmik.bhagvad_gita.presentation.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Home
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.app.HomeRoutes
import com.shub39.dharmik.bhagvad_gita.presentation.home.components.ChaptersSection
import com.shub39.dharmik.bhagvad_gita.presentation.home.components.HomeSection
import com.shub39.dharmik.bhagvad_gita.presentation.home.components.SettingsSection
import com.shub39.dharmik.core.presentation.components.PageFill
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.chapters
import dharmik.composeapp.generated.resources.home
import dharmik.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Home(
    onNavigateToVerses: () -> Unit,
    homeState: HomeState,
    onAction: (HomeAction) -> Unit
) = PageFill {
    val homeNavController = rememberNavController()

    var currentDest: HomeRoutes by remember { mutableStateOf(HomeRoutes.HomeSection) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            listOf(
                HomeRoutes.HomeSection,
                HomeRoutes.ChaptersSection,
                HomeRoutes.SettingsSection
            ).forEach { route ->
                item(
                    selected = currentDest == route,
                    onClick = {
                        if (currentDest != route) {
                            homeNavController.navigate(route) {
                                launchSingleTop = true
                                popUpTo(HomeRoutes.HomeSection)
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = when (route) {
                                HomeRoutes.HomeSection -> Icons.Rounded.Home
                                HomeRoutes.ChaptersSection -> Icons.Rounded.Book
                                HomeRoutes.SettingsSection -> Icons.Default.Settings
                            },
                            contentDescription = "Sections",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    label = {
                        Text(
                            text = stringResource(
                                when (route) {
                                    HomeRoutes.HomeSection -> Res.string.home
                                    HomeRoutes.ChaptersSection -> Res.string.chapters
                                    HomeRoutes.SettingsSection -> Res.string.settings
                                }
                            )
                        )
                    }
                )
            }
        },
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeFlexibleTopAppBar(
                    title = {
                        Text(stringResource(Res.string.app_name))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
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
                                imageVector = FontAwesomeIcons.Brands.Github,
                                contentDescription = "Github",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                uriHandler.openUri("https://buymeacoffee.com/shub39")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Coffee,
                                contentDescription = "Github",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { padding ->
            NavHost(
                modifier = Modifier.padding(padding),
                navController = homeNavController,
                startDestination = HomeRoutes.HomeSection,
                enterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
                exitTransition = { fadeOut(tween(300)) },
                popEnterTransition = { slideInVertically(tween(300), initialOffsetY = { it / 2 }) },
                popExitTransition = { fadeOut(tween(300)) }
            ) {
                composable<HomeRoutes.HomeSection> {
                    currentDest = HomeRoutes.HomeSection
                    HomeSection(
                        onNavigateToVerses = onNavigateToVerses,
                        homeState = homeState,
                        onAction = onAction
                    )
                }

                composable<HomeRoutes.ChaptersSection> {
                    currentDest = HomeRoutes.ChaptersSection
                    ChaptersSection(
                        onNavigateToVerses = onNavigateToVerses,
                        state = homeState,
                        onAction = onAction
                    )
                }

                composable<HomeRoutes.SettingsSection> {
                    currentDest = HomeRoutes.SettingsSection
                    SettingsSection(
                        state = homeState,
                        onAction = onAction
                    )
                }
            }
        }
    }
}