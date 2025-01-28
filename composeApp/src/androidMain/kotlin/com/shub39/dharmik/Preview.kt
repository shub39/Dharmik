package com.shub39.dharmik

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.shub39.dharmik.atharva_veda.domain.AvVerse
import com.shub39.dharmik.atharva_veda.presentation.AvState
import com.shub39.dharmik.atharva_veda.presentation.AvVersesPage
import com.shub39.dharmik.core.domain.AppThemes
import com.shub39.dharmik.core.presentation.theme.DharmikTheme

@Preview
@Composable
fun AvVersesPreview() = DharmikTheme(
    darkTheme = true,
    appTheme = AppThemes.YELLOW
) {
    AvVersesPage(
        navController = rememberNavController(),
        state = AvState(
            currentKaandas = (0..100).map { 
                AvVerse(
                    veda = "Random Veda",
                    samhita = "xyz",
                    kaanda = it,
                    sukta = it,
                    text = "Verse $it"
                )
            }
        ),
        action = {}
    )
}