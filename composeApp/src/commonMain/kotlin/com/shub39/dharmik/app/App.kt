package com.shub39.dharmik.app

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.bhagvad_gita.presentation.home.Home
import com.shub39.dharmik.bhagvad_gita.presentation.verses.Verses
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.BgViewModel
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.HomeViewModel
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    homevm: HomeViewModel = koinViewModel(),
    bgvm: BgViewModel = koinViewModel(),
) {
    val bgState by bgvm.state.collectAsStateWithLifecycle()
    val homeState by homevm.state.collectAsStateWithLifecycle()

    DharmikTheme(
        theme = homeState.theme
    ) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            composable<Routes.Home> {
                Home(
                    navController = navController,
                    homeState = homeState,
                    onAction = homevm::onAction
                )
            }

            composable<Routes.Verses> {
                Verses(
                    navController = navController,
                    state = bgState,
                    action = bgvm::onAction
                )
            }

            composable<Routes.FavVerses> {
                Verses(
                    navController = navController,
                    state = bgState,
                    action = bgvm::onAction,
                    favorites = true
                )
            }
        }
    }
}