package com.shub39.dharmik.core.presentation

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import java.awt.datatransfer.StringSelection

actual suspend fun copyToClipboard(clipboard: Clipboard, text: String) {
    clipboard.setClipEntry(
        ClipEntry(StringSelection(text))
    )
}