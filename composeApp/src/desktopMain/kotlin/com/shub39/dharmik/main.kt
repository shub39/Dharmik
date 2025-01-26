package com.shub39.dharmik

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dharmik",
    ) {
        App()
    }
}