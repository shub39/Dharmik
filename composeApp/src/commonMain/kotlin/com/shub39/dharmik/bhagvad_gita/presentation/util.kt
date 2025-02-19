package com.shub39.dharmik.bhagvad_gita.presentation

fun String.removeExtraLineBreaks(): String {
    return replace(Regex("[\r\n]{2,}"), "\n")
}