package com.shub39.dharmik.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.core.presentation.components.ContentCap
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.baseline_bookmark_24
import dharmik.composeapp.generated.resources.baseline_favorite_border_24
import dharmik.composeapp.generated.resources.baseline_settings_24
import dharmik.composeapp.generated.resources.round_library_books_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    DharmikTheme {
        val navController = rememberNavController()
        val destinations = listOf(
            Routes.SavedPage,
            Routes.FavoritesPage,
            Routes.LibraryPage,
            Routes.SettingsPage
        )
        var currentDest by remember { mutableStateOf(destinations[0]) }

        NavHost(
            navController = navController,
            startDestination = Routes.HomeGraph,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            composable<Routes.HomeGraph> {
                ContentCap {
                    Scaffold(
                        modifier = Modifier.widthIn(max = 500.dp),
                        topBar = {
                            LargeTopAppBar(
                                title = {
                                    Text(stringResource(Res.string.app_name))
                                }
                            )
                        },
                        bottomBar = {
                            BottomAppBar(
                                modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            ) {
                                NavigationBar {
                                    destinations.forEach { dest ->
                                        NavigationBarItem(
                                            selected = currentDest == dest,
                                            onClick = { currentDest = dest },
                                            icon = {
                                                Icon(
                                                    painter = painterResource(
                                                        when (dest) {
                                                            Routes.FavoritesPage -> Res.drawable.baseline_favorite_border_24
                                                            Routes.LibraryPage -> Res.drawable.round_library_books_24
                                                            Routes.SavedPage -> Res.drawable.baseline_bookmark_24
                                                            else -> Res.drawable.baseline_settings_24
                                                        }
                                                    ),
                                                    contentDescription = "Navigation Icon"
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) {

                    }
                }
            }
        }
    }
}