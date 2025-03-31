package com.shub39.dharmik.core.presentation.theme

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.shub39.dharmik.core.domain.AppTheme
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.noto_regular
import org.jetbrains.compose.resources.FontResource

data class Theme(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val withAmoled: Boolean = false,
    val seedColor: Color = Color.Yellow,
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val fontResource: FontResource = Res.font.noto_regular,
    val materialYou: Boolean = false
)
