package com.shub39.dharmik.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.shub39.dharmik.core.data.DataStoreFactory

actual val platformModule = module {
    singleOf(::DataStoreFactory)
}