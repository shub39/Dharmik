package com.shub39.dharmik

import android.app.Application
import com.shub39.dharmik.di.initKoin
import org.koin.android.ext.koin.androidContext

class DharmikApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DharmikApp)
        }
    }
}