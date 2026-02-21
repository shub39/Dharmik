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
package com.shub39.dharmik.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shub39.dharmik.bhagvad_gita.data.BgRepoImpl
import com.shub39.dharmik.bhagvad_gita.data.OnlineAudioSourceImpl
import com.shub39.dharmik.bhagvad_gita.domain.AudioSource
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.presentation.viewmodels.HomeViewModel
import com.shub39.dharmik.bhagvad_gita.presentation.viewmodels.StateLayer
import com.shub39.dharmik.bhagvad_gita.presentation.viewmodels.VersesViewModel
import com.shub39.dharmik.core.data.DataStoreFactory
import com.shub39.dharmik.core.data.DatabaseFactory
import com.shub39.dharmik.core.data.DharmikDb
import com.shub39.dharmik.core.data.PreferencesRepoImpl
import com.shub39.dharmik.core.domain.PreferencesRepo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // Database and Daos
    single {
        get<DatabaseFactory>()
            .create()
            .fallbackToDestructiveMigration(true)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<DharmikDb>().bgDao }

    // DataStore
    single(named("PreferencesDataStore")) { get<DataStoreFactory>().getPreferencesDataStore() }

    // Repositories
    singleOf(::OnlineAudioSourceImpl).bind<AudioSource>()

    single<PreferencesRepo> { PreferencesRepoImpl(get(named("PreferencesDataStore"))) }
    singleOf(::BgRepoImpl).bind<BgRepo>()

    // ViewModels
    singleOf(::StateLayer)
    viewModelOf(::HomeViewModel)
    viewModelOf(::VersesViewModel)
}
