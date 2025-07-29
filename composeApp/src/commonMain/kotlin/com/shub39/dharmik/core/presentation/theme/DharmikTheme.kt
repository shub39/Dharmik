package com.shub39.dharmik.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.*
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.shub39.dharmik.core.domain.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DharmikTheme(
    theme: Theme = Theme(),
    content: @Composable () -> Unit
) {
    DynamicMaterialExpressiveTheme(
        isDark = when(theme.appTheme) {
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
            else -> isSystemInDarkTheme()
        },
        isAmoled = theme.withAmoled,
        seedColor = theme.seedColor,
        style = theme.paletteStyle,
        typography = provideTypography(1f),
        content = content
    )
}