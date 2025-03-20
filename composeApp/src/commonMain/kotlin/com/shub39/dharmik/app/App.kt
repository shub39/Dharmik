package com.shub39.dharmik.app

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.bhagvad_gita.presentation.BgChaptersPage
import com.shub39.dharmik.bhagvad_gita.presentation.BgVersesPage
import com.shub39.dharmik.bhagvad_gita.presentation.BgViewModel
import com.shub39.dharmik.core.presentation.home.Home
import com.shub39.dharmik.core.presentation.home.SettingsUseCase
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    settingsUseCase: SettingsUseCase = koinInject(),
    bgvm: BgViewModel = koinViewModel()
) {
    val isDark by settingsUseCase.getPrefIsDarkTheme().collectAsStateWithLifecycle(true)

    val bgState by bgvm.bgState.collectAsStateWithLifecycle()

    DharmikTheme(
        darkTheme = isDark
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
                    bgState = bgState,
                    bgAction = bgvm::onAction
                )
            }

            navigation<Routes.BhagvadGitaGraph>(
                startDestination = Routes.BgChaptersPage
            ) {
                composable<Routes.BgChaptersPage> {
                    BgChaptersPage(
                        navController = navController,
                        state = bgState,
                        action = bgvm::onAction
                    )
                }

                composable<Routes.BgChapterVersesPage> {
                   BgVersesPage(
                       navController = navController,
                       state = bgState,
                       action = bgvm::onAction
                   )
                }

                composable<Routes.BgFavVersesPage> {
                    BgVersesPage(
                        navController = navController,
                        state = bgState,
                        action = bgvm::onAction,
                        favorites = true
                    )
                }
            }
        }
    }
}