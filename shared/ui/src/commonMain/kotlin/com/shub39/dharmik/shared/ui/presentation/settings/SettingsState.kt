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
package com.shub39.dharmik.shared.ui.presentation.settings

import com.shub39.dharmik.shared.core.domain.BulkAudioState
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.ui.theme.Theme

data class SettingsState(
    val fontSize: Float = 16f,
    val verseCardState: VerseCardState = VerseCardState.ENGLISH,
    val theme: Theme = Theme(),
    val bulkAudio: BulkAudioState = BulkAudioState.Checking,
)
