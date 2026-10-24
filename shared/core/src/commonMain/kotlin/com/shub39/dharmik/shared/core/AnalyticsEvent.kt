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
package com.shub39.dharmik.shared.core

/**
 * A named analytics event.
 *
 * Names are snake_case and describe *what happened*, never *who* it happened to: no personal data
 * may ever be encoded in the name. Context that identifies the subject of an event (chapter and
 * verse numbers, the selected theme, ...) travels as a property instead.
 */
@JvmInline
value class AnalyticsEvent(val name: String) {
    companion object {
        val APP_OPENED = AnalyticsEvent("app_opened")

        val HOME_VIEWED = AnalyticsEvent("home_viewed")
        val CHAPTERS_VIEWED = AnalyticsEvent("chapters_viewed")
        val CHAPTER_OPENED = AnalyticsEvent("chapter_opened")

        val VERSES_VIEWED = AnalyticsEvent("verses_viewed")
        val VERSE_CHANGED = AnalyticsEvent("verse_changed")
        val VERSE_FAVORITED = AnalyticsEvent("verse_favorited")
        val VERSE_UNFAVORITED = AnalyticsEvent("verse_unfavorited")
        val VERSE_CARD_STATE_CHANGED = AnalyticsEvent("verse_card_state_changed")

        val AUDIO_PLAYED = AnalyticsEvent("audio_played")
        val AUDIO_PAUSED = AnalyticsEvent("audio_paused")
        val AUDIO_AUTOPLAY_CHANGED = AnalyticsEvent("audio_autoplay_changed")

        val BULK_AUDIO_DOWNLOAD_STARTED = AnalyticsEvent("bulk_audio_download_started")
        val BULK_AUDIO_DOWNLOAD_COMPLETED = AnalyticsEvent("bulk_audio_download_completed")
        val BULK_AUDIO_DOWNLOAD_FAILED = AnalyticsEvent("bulk_audio_download_failed")
        val BULK_AUDIO_DOWNLOAD_CANCELLED = AnalyticsEvent("bulk_audio_download_cancelled")
        val BULK_AUDIO_DELETED = AnalyticsEvent("bulk_audio_deleted")

        val SETTINGS_VIEWED = AnalyticsEvent("settings_viewed")
        val THEME_CHANGED = AnalyticsEvent("theme_changed")
        val FONT_SIZE_CHANGED = AnalyticsEvent("font_size_changed")
        val ABOUT_VIEWED = AnalyticsEvent("about_viewed")
        val CHANGELOG_VIEWED = AnalyticsEvent("changelog_viewed")

        val PAYWALL_OPENED = AnalyticsEvent("paywall_opened")
        val PAYWALL_PURCHASED = AnalyticsEvent("paywall_purchased")
    }
}
