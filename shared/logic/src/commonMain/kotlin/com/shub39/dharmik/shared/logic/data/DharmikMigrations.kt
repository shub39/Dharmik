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

import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

/**
 * Hand written migrations.
 *
 * The database is opened with `fallbackToDestructiveMigration`, so a version bump without a
 * matching migration here silently wipes user data (favourites included). Always add a [Migration].
 */
object DharmikMigrations {

    /** Adds the `collections` and `collection_verses` tables introduced by version 2. */
    val MIGRATION_1_2: Migration =
        Migration(1, 2) { connection ->
            connection.execSQL(
                "CREATE TABLE IF NOT EXISTS `collections` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `position` INTEGER NOT NULL)"
            )
            connection.execSQL(
                "CREATE TABLE IF NOT EXISTS `collection_verses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `collectionId` INTEGER NOT NULL, `position` INTEGER NOT NULL, `chapter` INTEGER NOT NULL, `verse` INTEGER NOT NULL, `notes` TEXT NOT NULL, FOREIGN KEY(`collectionId`) REFERENCES `collections`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
            )
            connection.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_collection_verses_collectionId` ON `collection_verses` (`collectionId`)"
            )
        }

    val ALL = listOf(MIGRATION_1_2)
}
