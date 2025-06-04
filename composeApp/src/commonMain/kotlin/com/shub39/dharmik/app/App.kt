package com.shub39.dharmik.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.HomeViewModel
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.VersesViewModel
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    homevm: HomeViewModel = koinViewModel(),
    bgvm: VersesViewModel = koinViewModel(),
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
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) }
        ) {
            composable<Routes.Home> {
                Home(
                    onNavigateToVerses = {
                        navController.navigate(Routes.Verses) {
                            launchSingleTop = true
                            popUpTo(Routes.Home)
                        }
                    },
                    homeState = homeState,
                    onAction = homevm::onAction
                )
            }

            composable<Routes.Verses> {
                Verses(
                    state = bgState,
                    action = bgvm::onAction,
                    onBack = {
                        navController.navigate(Routes.Home) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}