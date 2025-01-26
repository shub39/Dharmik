package com.shub39.dharmik.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.shub39.dharmik.core.domain.AppThemes

@Composable
fun DharmikTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppThemes = AppThemes.YELLOW,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when(appTheme) {
            else -> if (darkTheme) darkYellow else lightYellow
           // AppThemes.LIME -> if (darkTheme) darkLime else lightLime
           // AppThemes.BLUE -> if (darkTheme) darkBlue else lightBlue
        },
        content = content
    )
}