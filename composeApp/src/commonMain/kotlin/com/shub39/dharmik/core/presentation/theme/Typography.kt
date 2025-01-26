package com.shub39.dharmik.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.poppins_black
import dharmik.composeapp.generated.resources.poppins_bold
import dharmik.composeapp.generated.resources.poppins_mediun
import dharmik.composeapp.generated.resources.poppins_regular
import org.jetbrains.compose.resources.Font

@Composable
fun provideTypography(scale: Float = 1f): Typography {
    val poppinsFont = FontFamily(
        Font(Res.font.poppins_regular, FontWeight.Normal),
        Font(Res.font.poppins_bold, FontWeight.Bold),
        Font(Res.font.poppins_mediun, FontWeight.Medium),
        Font(Res.font.poppins_black, FontWeight.Black)
    )

    return Typography(
        displayLarge = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Black,
            fontSize = 57.sp * scale,
            lineHeight = 64.sp * scale,
            letterSpacing = -(0.25).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Black,
            fontSize = 45.sp * scale,
            lineHeight = 52.sp * scale
        ),
        displaySmall = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Black,
            fontSize = 36.sp * scale,
            lineHeight = 44.sp * scale
        ),
        headlineLarge = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp * scale,
            lineHeight = 40.sp * scale
        ),
        headlineMedium = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp * scale,
            lineHeight = 36.sp * scale
        ),
        headlineSmall = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp * scale,
            lineHeight = 32.sp * scale
        ),
        titleLarge = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp * scale,
            lineHeight = 28.sp * scale
        ),
        titleMedium = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp * scale,
            lineHeight = 24.sp * scale,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp * scale,
            lineHeight = 20.sp * scale,
            letterSpacing = 0.1.sp
        ),
        labelLarge = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp * scale,
            lineHeight = 16.sp * scale,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp * scale,
            lineHeight = 14.sp * scale,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp * scale,
            lineHeight = 12.sp * scale,
            letterSpacing = 0.5.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp * scale,
            lineHeight = 24.sp * scale,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * scale,
            lineHeight = 20.sp * scale,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = poppinsFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp * scale,
            lineHeight = 16.sp * scale,
            letterSpacing = 0.4.sp
        )
    )
}