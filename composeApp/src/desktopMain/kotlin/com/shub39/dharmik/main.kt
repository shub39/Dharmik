package com.shub39.dharmik

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.shub39.dharmik.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dharmik",
    ) {
        App()
    }
}