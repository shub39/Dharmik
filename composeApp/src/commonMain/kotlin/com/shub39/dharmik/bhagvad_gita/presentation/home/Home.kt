package com.shub39.dharmik.bhagvad_gita.presentation.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.app.HomeRoutes
import com.shub39.dharmik.bhagvad_gita.domain.Commentaries
import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.bhagvad_gita.domain.Translations
import com.shub39.dharmik.bhagvad_gita.presentation.home.components.ChaptersSection
import com.shub39.dharmik.bhagvad_gita.presentation.home.components.HomeSection
import com.shub39.dharmik.bhagvad_gita.presentation.home.components.SettingsSection
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.presentation.components.PageFill
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import com.shub39.dharmik.core.presentation.theme.Theme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Book
import compose.icons.fontawesomeicons.solid.Home
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.chapters
import dharmik.composeapp.generated.resources.home
import dharmik.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(Res.string.app_name))
                },
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
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp
                    )
                )
            ) {
                listOf(
                    HomeRoutes.HomeSection,
                    HomeRoutes.ChaptersSection,
                    HomeRoutes.SettingsSection
                ).forEach { route ->
                    NavigationBarItem(
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
                                    HomeRoutes.HomeSection -> FontAwesomeIcons.Solid.Home
                                    HomeRoutes.ChaptersSection -> FontAwesomeIcons.Solid.Book
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
            }
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

@Preview
@Composable
private fun Preview() {
    DharmikTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        Home(
            onNavigateToVerses = {},
            homeState = HomeState(
                favorites = (0..100).map {
                    GitaVerse(
                        chapter = it.toLong(),
                        verse = it.toLong(),
                        text = "Verse $it",
                        commentaries = Commentaries(),
                        translations = Translations()
                    )
                }
            ),
            onAction = {}
        )
    }
}