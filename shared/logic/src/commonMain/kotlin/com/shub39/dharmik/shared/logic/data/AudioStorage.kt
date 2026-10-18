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

import org.koin.core.annotation.Single

/** Platform specific location of the app's audio directory and URI helpers. */
@Single
expect class AudioStorage {
    /** Absolute path of the directory that holds every downloaded audio file. */
    fun audioDirPath(): String

    /** Converts an absolute file path to a `file://` URI that the player can consume. */
    fun toFileUri(path: String): String
}
