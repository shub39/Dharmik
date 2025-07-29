package com.shub39.dharmik.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.shub39.dharmik.core.domain.AppTheme

@Composable
fun DharmikTheme(
    theme: Theme = Theme(),
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
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