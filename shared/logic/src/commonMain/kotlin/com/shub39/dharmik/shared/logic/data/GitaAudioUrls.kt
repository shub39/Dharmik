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
package com.shub39.dharmik.shared.logic.data

import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.GITA_AUDIO_BASE_URL

/**
 * Builds the per-verse MP3 URLs on the legacy Gita Supersite host.
 *
 * The path templates are unchanged from the original implementation; only the host moved to
 * `old.gitasupersite.in` (see [GITA_AUDIO_BASE_URL]).
 */
internal object GitaAudioUrls {
    fun mool(chapter: Long, verse: Long): String =
        "$GITA_AUDIO_BASE_URL/CHAP$chapter/$chapter-$verse.MP3"

    fun english(chapter: Long, verse: Long): String =
        "$GITA_AUDIO_BASE_URL/Purohit/$chapter.$verse.mp3"

    fun hindi(chapter: Long, verse: Long): String =
        "$GITA_AUDIO_BASE_URL/Tejomayananda/chapter/C$chapter-H-${verse.toString().padStart(2, '0')}.mp3"

    fun url(chapter: Long, verse: Long, variant: AudioVariant): String =
        when (variant) {
            AudioVariant.MOOL -> mool(chapter, verse)
            AudioVariant.ENGLISH -> english(chapter, verse)
            AudioVariant.HINDI -> hindi(chapter, verse)
        }
}
