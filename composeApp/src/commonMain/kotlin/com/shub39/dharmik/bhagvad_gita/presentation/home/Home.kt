package com.shub39.dharmik.bhagvad_gita.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.app.HomeRoutes
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.presentation.components.PageFill
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import com.shub39.dharmik.core.presentation.theme.Theme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Book
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Home
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.home
import dharmik.composeapp.generated.resources.library
import dharmik.composeapp.generated.resources.reminders
import dharmik.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    homeState: HomeState,
    onAction: (HomeAction) -> Unit
) = PageFill {
    val homeNavController = rememberNavController()

    var currentDest: HomeRoutes by remember { mutableStateOf(HomeRoutes.HomeSection) }

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(Res.string.app_name))
                }
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
                    HomeRoutes.LibrarySection,
                    HomeRoutes.RemindersSection,
                    HomeRoutes.SettingsSection
                ).forEach { route ->
                    NavigationBarItem(
                        selected = currentDest == route,
                        onClick = { homeNavController.navigate(route) },
                        icon = {
                            Icon(
                                imageVector =  when (route) {
                                    HomeRoutes.HomeSection -> FontAwesomeIcons.Solid.Home
                                    HomeRoutes.LibrarySection -> FontAwesomeIcons.Solid.Book
                                    HomeRoutes.RemindersSection -> FontAwesomeIcons.Solid.Clock
                                    HomeRoutes.SettingsSection -> Icons.Default.Settings
                                },
                                contentDescription = "Sections",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    when (route) {
                                        HomeRoutes.HomeSection -> Res.string.home
                                        HomeRoutes.LibrarySection -> Res.string.library
                                        HomeRoutes.RemindersSection -> Res.string.reminders
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
            startDestination = HomeRoutes.HomeSection
        ) {
            composable<HomeRoutes.HomeSection> {
                currentDest = HomeRoutes.HomeSection
            }

            composable<HomeRoutes.LibrarySection>{
                currentDest = HomeRoutes.LibrarySection
            }

            composable<HomeRoutes.RemindersSection>{
                currentDest = HomeRoutes.RemindersSection
            }

            composable<HomeRoutes.SettingsSection>{
                currentDest = HomeRoutes.SettingsSection
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    DharmikTheme(
        theme = Theme(
            appTheme = AppTheme.SYSTEM
        )
    ) {
        Home(
            navController = rememberNavController(),
            homeState = HomeState(),
            onAction = {}
        )
    }
}