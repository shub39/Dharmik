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
package com.shub39.dharmik.shared.logic.data

import java.io.File

/** Resolves the per-user application data directory on desktop JVM targets. */
internal fun resolveAppDataDir(): File {
    val os = System.getProperty("os.name")?.lowercase().orEmpty()
    val userHome = System.getProperty("user.home")

    return when {
        os.contains("win") -> File(System.getenv("APPDATA"), "Dharmik")
        os.contains("mac") -> File(userHome, "Library/Application Support/Dharmik")
        else -> File(userHome, ".local/share/Dharmik")
    }
}
