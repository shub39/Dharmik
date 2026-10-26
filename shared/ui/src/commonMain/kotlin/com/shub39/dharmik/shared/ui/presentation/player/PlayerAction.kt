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

import com.shub39.dharmik.shared.core.domain.AudioVariant

sealed interface PlayerAction {
    data object PlayPause : PlayerAction

    data object Next : PlayerAction

    data object Previous : PlayerAction

    data class SeekTo(val positionMillis: Long) : PlayerAction

    /** Jumps to a track of the playlist shown in the queue sheet. */
    data class SelectTrack(val index: Int) : PlayerAction

    data class SetVariant(val variant: AudioVariant) : PlayerAction

    /** Retries the current track after a playback error. */
    data object Retry : PlayerAction

    data object OpenQueue : PlayerAction

    data object DismissQueue : PlayerAction
}
