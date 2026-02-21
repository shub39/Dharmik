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
package com.shub39.dharmik.core.domain

import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.dark_theme
import dharmik.composeapp.generated.resources.light_theme
import dharmik.composeapp.generated.resources.system_theme
import org.jetbrains.compose.resources.StringResource

enum class AppTheme(val label: StringResource) {
    LIGHT(Res.string.light_theme),
    DARK(Res.string.dark_theme),
    SYSTEM(Res.string.system_theme),
}
