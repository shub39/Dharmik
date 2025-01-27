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
import com.shub39.dharmik.atharva_veda.presentation.AvKaandasPage
import com.shub39.dharmik.atharva_veda.presentation.AvVersesPage
import com.shub39.dharmik.atharva_veda.presentation.AvViewModel
import com.shub39.dharmik.core.domain.AppThemes
import com.shub39.dharmik.core.presentation.home.Home
import com.shub39.dharmik.core.presentation.home.SettingsUseCase
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import org.koin.compose.koinInject

@Composable
fun App(
    settingsUseCase: SettingsUseCase = koinInject(),
    avvm: AvViewModel = koinInject()
) {
    val isDark by settingsUseCase.getPrefIsDarkTheme().collectAsStateWithLifecycle(false)
    val appTheme by settingsUseCase.getPrefAppTheme().collectAsStateWithLifecycle(AppThemes.YELLOW)

    val avState by avvm.kaandas.collectAsStateWithLifecycle()

    DharmikTheme(
        darkTheme = isDark,
        appTheme = appTheme
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
                    avState = avState,
                    avAction = avvm::onAction
                )
            }

            navigation<Routes.AtharvaVedaGraph>(
                startDestination = Routes.AvKaandasPage
            ) {
                composable<Routes.AvKaandasPage> {
                    AvKaandasPage(
                        navController = navController,
                        state = avState,
                        action = avvm::onAction
                    )
                }

                composable<Routes.AvVersesPage> {
                    AvVersesPage(
                        navController = navController,
                        state = avState,
                        action = avvm::onAction
                    )
                }
            }
        }
    }
}