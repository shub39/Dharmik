package com.shub39.dharmik

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.dharmik.app.App
import com.shub39.dharmik.di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        state = WindowState(),
        title = "Dharmik"
    ) {
        App()
    }
}