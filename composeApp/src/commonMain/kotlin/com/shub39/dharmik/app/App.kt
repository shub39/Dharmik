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
import com.shub39.dharmik.core.domain.AppThemes
import com.shub39.dharmik.core.presentation.home.Home
import com.shub39.dharmik.core.presentation.home.SettingsUseCase
import com.shub39.dharmik.core.presentation.theme.DharmikTheme
import org.koin.compose.koinInject

@Composable
fun App(
    settingsUseCase: SettingsUseCase = koinInject()
) {
    val isDark by settingsUseCase.getPrefIsDarkTheme().collectAsStateWithLifecycle(false)
    val appTheme by settingsUseCase.getPrefAppTheme().collectAsStateWithLifecycle(AppThemes.YELLOW)

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
                Home()
            }

            composable<Routes.AtharvaVedaGraph> {

            }
        }
    }
}