/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.ui.presentation.verses

import androidx.compose.foundation.pager.PagerState
import com.shub39.dharmik.shared.core.domain.Audios
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.NoOpMediaPlayer

data class VersesState(
    val currentVerses: List<GitaVerse> = emptyList(),
    val audioFiles: List<Audios> = emptyList(),
    val saveBookMarks: Boolean = true,
    val autoPlay: Boolean = false,
    val isPlaying: Boolean = false,
    val favorites: List<GitaVerse> = emptyList(),
    val player: MediaPlayer = NoOpMediaPlayer(),
    val verseCardState: VerseCardState = VerseCardState.SANSKRIT,
    val pagerState: PagerState = PagerState { 0 },
    val fontSize: Float = 16f,
)
