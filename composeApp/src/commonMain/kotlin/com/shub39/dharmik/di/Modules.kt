package com.shub39.dharmik.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shub39.dharmik.DharmikConfig
import com.shub39.dharmik.bhagvad_gita.data.BgRepoImpl
import com.shub39.dharmik.bhagvad_gita.data.OfflineAudioSourceImpl
import com.shub39.dharmik.bhagvad_gita.data.OnlineAudioSourceImpl
import com.shub39.dharmik.bhagvad_gita.domain.AudioSource
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.VersesViewModel
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.HomeViewModel
import com.shub39.dharmik.bhagvad_gita.presentation.viewModels.StateLayer
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
    if (DharmikConfig.variant == "offline") {
        singleOf(::OfflineAudioSourceImpl).bind<AudioSource>()
    } else {
        singleOf(::OnlineAudioSourceImpl).bind<AudioSource>()
    }

    single<PreferencesRepo> { PreferencesRepoImpl(get(named("PreferencesDataStore"))) }
    singleOf(::BgRepoImpl).bind<BgRepo>()

    // ViewModels
    singleOf(::StateLayer)
    viewModelOf(::HomeViewModel)
    viewModelOf(::VersesViewModel)
}