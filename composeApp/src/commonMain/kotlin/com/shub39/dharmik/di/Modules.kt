package com.shub39.dharmik.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shub39.dharmik.atharva_veda.domain.AvKaandasRepo
import com.shub39.dharmik.atharva_veda.data.AvKaandasRepoImpl
import com.shub39.dharmik.atharva_veda.presentation.AvViewModel
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.data.BgRepoImpl
import com.shub39.dharmik.bhagvad_gita.presentation.BgViewModel
import org.koin.core.qualifier.named
import com.shub39.dharmik.core.data.DataStoreFactory
import com.shub39.dharmik.core.data.PreferencesRepoImpl
import com.shub39.dharmik.core.data.DatabaseFactory
import com.shub39.dharmik.core.data.DharmikDb
import com.shub39.dharmik.core.domain.PreferencesRepo
import com.shub39.dharmik.core.presentation.home.SettingsUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // Database and Daos
    single {
        get<DatabaseFactory>()
            .create()
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<DharmikDb>().avDao }
    single { get<DharmikDb>().bgDao }

    // DataStore
    single(named("PreferencesDataStore")) { get<DataStoreFactory>().getPreferencesDataStore() }

    // Repositories
    single<PreferencesRepo> { PreferencesRepoImpl(get(named("PreferencesDataStore"))) }
    singleOf(::AvKaandasRepoImpl).bind<AvKaandasRepo>()
    singleOf(::BgRepoImpl).bind<BgRepo>()

    // Use Cases
    single<SettingsUseCase> { SettingsUseCase(get()) }

    // ViewModels
    viewModelOf(::AvViewModel)
    viewModelOf(::BgViewModel)
}