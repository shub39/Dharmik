package com.shub39.dharmik

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.dharmik.app.App
import com.shub39.dharmik.di.initKoin

fun main() {
    initKoin()
    singleWindowApplication(
        title = "Dharmik",
        state = WindowState(width = 800.dp, height = 800.dp),
        alwaysOnTop = true
    ) {
        App()
    }
}