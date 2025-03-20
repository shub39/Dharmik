package com.shub39.dharmik.core.domain

import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.dark_theme
import dharmik.composeapp.generated.resources.light_theme
import dharmik.composeapp.generated.resources.system_theme
import org.jetbrains.compose.resources.StringResource

enum class AppTheme(
    val label: StringResource
) {
    LIGHT(Res.string.light_theme),
    DARK(Res.string.dark_theme),
    SYSTEM(Res.string.system_theme)
}