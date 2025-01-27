package com.shub39.dharmik.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.shub39.dharmik.core.data.DataStoreFactory
import com.shub39.dharmik.core.data.DatabaseFactory
import org.koin.android.ext.koin.androidApplication

actual val platformModule = module {
    singleOf(::DataStoreFactory)
    single { DatabaseFactory(androidApplication()) }
}