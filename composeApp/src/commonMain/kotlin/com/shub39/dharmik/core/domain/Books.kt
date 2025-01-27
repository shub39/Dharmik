package com.shub39.dharmik.core.domain

import androidx.annotation.StringRes
import com.shub39.dharmik.app.Routes
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.atharva_veda_desc
import org.jetbrains.compose.resources.StringResource

enum class Books(
    val route: Routes,
    @StringRes val displayName: StringResource,
    @StringRes val description: StringResource
) {
    ATHARVA_VEDA(Routes.AtharvaVedaGraph, Res.string.atharva_veda, Res.string.atharva_veda_desc)
}