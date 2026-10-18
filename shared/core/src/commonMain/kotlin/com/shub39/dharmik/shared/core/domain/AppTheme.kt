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
package com.shub39.dharmik.shared.core.domain

/**
 * User-selectable app theme.
 *
 * NOTE: The original `composeApp` definition carried a Compose `StringResource` label. This module
 * is pure common Kotlin (no Compose resources), so the label is kept as a plain [String] holding
 * the same English display text. Localization can be layered on downstream in the UI module.
 */
enum class AppTheme(val label: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System"),
}
