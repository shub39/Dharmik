/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
    val seedColor: Color = Color(0xFFEBDBB2),
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val fontResource: FontResource = Res.font.noto_regular,
    val materialYou: Boolean = false,
)
