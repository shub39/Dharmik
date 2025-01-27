package com.shub39.dharmik.di

import com.shub39.dharmik.atharva_veda.domain.AvKaandasRepo
import com.shub39.dharmik.atharva_veda.data.AvKaandasRepoImpl
import com.shub39.dharmik.atharva_veda.presentation.AvViewModel
import org.koin.core.qualifier.named
import com.shub39.dharmik.core.data.DataStoreFactory
import com.shub39.dharmik.core.data.PreferencesRepoImpl
import com.shub39.dharmik.core.domain.PreferencesRepo
import com.shub39.dharmik.core.presentation.home.SettingsUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // DataStore
    single(named("PreferencesDataStore")) { get<DataStoreFactory>().getPreferencesDataStore() }

    // Repositories
    single<PreferencesRepo> { PreferencesRepoImpl(get(named("PreferencesDataStore"))) }
    singleOf(::AvKaandasRepoImpl).bind<AvKaandasRepo>()

    // Use Cases
    single<SettingsUseCase> { SettingsUseCase(get()) }

    // ViewModels
    viewModelOf(::AvViewModel)
}