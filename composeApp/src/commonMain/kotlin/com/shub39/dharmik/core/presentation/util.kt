package com.shub39.dharmik.core.presentation

import androidx.compose.ui.platform.Clipboard

expect suspend fun copyToClipboard(clipboard: Clipboard, text: String)