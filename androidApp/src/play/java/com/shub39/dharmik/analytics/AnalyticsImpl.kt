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
package com.shub39.dharmik.analytics

import com.posthog.PostHog
import com.shub39.dharmik.BuildConfig
import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import kotlin.time.Clock

/**
 * Sends events to PostHog. Only compiled into the `play` flavor.
 *
 * No personal data is ever attached: the identity is PostHog's anonymous per-install id and the
 * only properties are the app version and the non-identifying context passed in by the caller.
 */
class AnalyticsImpl : AnalyticsWrapper {

    private fun getDefaultProperties() =
        mapOf(
            "app_name" to "Dharmik",
            "app_version" to BuildConfig.VERSION_NAME,
            "time_stamp" to Clock.System.now().toEpochMilliseconds().div(1000),
        )

    override fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>) {
        try {
            PostHog.capture(event = event.name, properties = getDefaultProperties() + properties)
        } catch (_: Exception) {
            // Analytics must never break a user flow.
        }
    }
}
