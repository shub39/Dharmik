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
package com.shub39.dharmik.shared.ui.presentation.player

import com.shub39.dharmik.shared.core.domain.PlaybackState
import com.shub39.dharmik.shared.core.domain.VerseCardState

data class PlayerState(
    val playback: PlaybackState = PlaybackState(),
    /** Language of [verseText], which follows the audio variant that is playing. */
    val verseCardState: VerseCardState = VerseCardState.SANSKRIT,
    val verseText: String = "",
    /** Listening to playlists is a Plus feature; without it the screen only shows the paywall. */
    val isPlus: Boolean = false,
    val queueSheetOpen: Boolean = false,
)
