package com.shub39.dharmik

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.shub39.dharmik.app.App
import com.shub39.dharmik.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Dharmik",
        ) {
            App()
        }
    }
}