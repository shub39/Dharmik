package com.shub39.dharmik.bhagvad_gita.presentation.verses

import androidx.compose.foundation.pager.PagerState
import chaintech.videoplayer.host.MediaPlayerHost
import com.shub39.dharmik.bhagvad_gita.domain.Audios
import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.core.domain.VerseCardState

data class VersesState(
    val currentVerses: List<GitaVerse> = emptyList(),
    val audioFiles: List<Audios> = emptyList(),
    val saveBookMarks: Boolean = true,
    val autoPlay: Boolean = false,
    val isPlaying: Boolean = false,
    val favorites: List<GitaVerse> = emptyList(),
    val playerHost: MediaPlayerHost = MediaPlayerHost(isLooping = false),
    val verseCardState: VerseCardState = VerseCardState.SANSKRIT,
    val pagerState: PagerState = PagerState { 0 }
)