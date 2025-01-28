package com.shub39.dharmik.bhagvad_gita.presentation

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.bhagvad_gita.domain.GitaFile
import kotlinx.serialization.Serializable

@Serializable
data class BgState(
    val chapters: Int = 18,
    val currentFile: GitaFile? = null,
    val favorites: List<GitaVerse> = emptyList()
)
