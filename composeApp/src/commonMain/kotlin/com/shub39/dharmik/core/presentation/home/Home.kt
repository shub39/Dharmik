package com.shub39.dharmik.core.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.app.HomeRoutes
import com.shub39.dharmik.app.Routes
import com.shub39.dharmik.bhagvad_gita.presentation.BgAction
import com.shub39.dharmik.bhagvad_gita.presentation.BgState
import com.shub39.dharmik.core.presentation.components.PageFill
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.baseline_settings_24
import dharmik.composeapp.generated.resources.home
import dharmik.composeapp.generated.resources.library
import dharmik.composeapp.generated.resources.round_home_24
import dharmik.composeapp.generated.resources.round_library_books_24
import dharmik.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    bgState: BgState,
    bgAction: (BgAction) -> Unit
) = PageFill {
    var currentDest by rememberSaveable { mutableStateOf(0) }

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
                NavigationBar {
                    HomeRoutes.forEach { dest ->
                        NavigationBarItem(
                            selected = destinations[currentDest] == dest,
                            onClick = { currentDest = destinations.indexOf(dest) },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        when (dest) {
                                            Routes.HomeSection -> Res.drawable.round_home_24
                                            Routes.LibrarySection -> Res.drawable.round_library_books_24
                                            else -> Res.drawable.baseline_settings_24
                                        }
                                    ),
                                    contentDescription = "Navigation Icon"
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(
                                        when (dest) {
                                            Routes.HomeSection -> Res.string.home
                                            Routes.LibrarySection -> Res.string.library
                                            else -> Res.string.settings
                                        }
                                    )
                                )
                            },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = currentDest,
            modifier = Modifier.padding(padding)
        ) {
            when (it) {
                0 -> HomeSection(
                    navController = navController,
                    bgState = bgState,
                    bgAction = bgAction
                )
                1 -> LibrarySection(navController)
                else -> SettingsSection()
            }
        }
    }
}