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
package com.shub39.dharmik.core.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shub39.dharmik.bhagvad_gita.data.BgDao
import com.shub39.dharmik.bhagvad_gita.data.BgVerseEntity
import com.shub39.dharmik.bhagvad_gita.data.BgVerseTypeConverters

@Database(entities = [BgVerseEntity::class], exportSchema = false, version = 1)
@TypeConverters(BgVerseTypeConverters::class)
@ConstructedBy(DbConstructor::class)
abstract class DharmikDb : RoomDatabase() {
    abstract val bgDao: BgDao

    companion object {
        const val DB_NAME = "dharmik.db"
    }
}
