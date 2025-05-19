package com.shub39.dharmik

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.dharmik.app.App
import com.shub39.dharmik.di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        state = WindowState(size = DpSize(width = 700.dp, height = 700.dp)),
        title = "Dharmik"
    ) {
        App()
    }
}