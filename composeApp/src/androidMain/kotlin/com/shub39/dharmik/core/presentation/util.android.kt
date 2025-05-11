package com.shub39.dharmik.core.presentation

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun copyToClipboard(clipboard: Clipboard, text: String) {
    clipboard.setClipEntry(
        ClipEntry(ClipData.newPlainText("Verse", text))
    )
}