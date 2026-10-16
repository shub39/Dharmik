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
package com.shub39.dharmik.shared.core.domain

val slokaNumbers: List<Int> =
    listOf(47, 72, 43, 42, 29, 47, 30, 28, 34, 42, 55, 20, 35, 27, 20, 24, 28, 78)

// ---- Audio sources ---------------------------------------------------------

/** GitHub repository that publishes the bulk audio asset. */
const val DHARMIK_REPO = "shub39/Dharmik"

/** Base URL of the GitHub Releases page (stable `latest/download` path). */
const val GITHUB_RELEASES_URL = "https://github.com/$DHARMIK_REPO/releases"

/** Manifest describing the current bulk audio asset. */
const val GITA_AUDIO_MANIFEST_URL = "$GITHUB_RELEASES_URL/latest/download/gita-audio.json"

/**
 * Legacy Gita Supersite host that still serves the per-verse MP3s.
 *
 * The modern `www.gitasupersite.iitk.ac.in` host now returns a landing page instead of media.
 */
const val GITA_AUDIO_BASE_URL = "https://old.gitasupersite.in/sites/default/files/audio"
