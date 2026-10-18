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

import androidx.room3.Room
import androidx.room3.RoomDatabase
import java.io.File
import org.koin.core.annotation.Single

@Single
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<DharmikDb> {
        val appDataDir = resolveAppDataDir()
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        val dbFile = File(appDataDir, DharmikDb.DB_NAME)

        return Room.databaseBuilder<DharmikDb>(dbFile.absolutePath)
    }
}
