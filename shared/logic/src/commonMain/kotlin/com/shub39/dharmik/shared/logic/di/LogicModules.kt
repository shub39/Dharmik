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
package com.shub39.dharmik.shared.logic.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.MediaPlayerFactory
import com.shub39.dharmik.shared.logic.data.BgDao
import com.shub39.dharmik.shared.logic.data.CollectionsDao
import com.shub39.dharmik.shared.logic.data.DataStoreFactory
import com.shub39.dharmik.shared.logic.data.DatabaseFactory
import com.shub39.dharmik.shared.logic.data.DharmikDb
import com.shub39.dharmik.shared.logic.data.DharmikMigrations
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.shub39.dharmik.shared.logic")
class LogicModules {

    @Single
    fun getDharmikDb(factory: DatabaseFactory): DharmikDb =
        factory
            .create()
            .addMigrations(*DharmikMigrations.ALL.toTypedArray())
            .fallbackToDestructiveMigration(true)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .build()

    @Single fun getBgDao(db: DharmikDb): BgDao = db.bgDao

    @Single fun getCollectionsDao(db: DharmikDb): CollectionsDao = db.collectionsDao

    @Single
    fun getPreferencesDataStore(factory: DataStoreFactory): DataStore<Preferences> =
        factory.getPreferencesDataStore()

    @Single
    fun getHttpClient(): HttpClient = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 30_000
            // Abort a transfer that stops receiving data so the bulk downloader can resume it
            // instead of blocking forever. No requestTimeout: a 360 MB archive legitimately
            // takes minutes to download.
            socketTimeoutMillis = 30_000
        }
    }

    @Single fun getMediaPlayer(factory: MediaPlayerFactory): MediaPlayer = factory.create()
}
