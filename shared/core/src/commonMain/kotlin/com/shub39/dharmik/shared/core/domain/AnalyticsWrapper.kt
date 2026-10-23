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

import com.shub39.dharmik.shared.core.AnalyticsEvent

/**
 * Sink for analytics events.
 *
 * This is the only seam between the shared modules and an actual analytics backend: the
 * implementations live in the platform apps, so the shared code never depends on a tracking SDK.
 */
interface AnalyticsWrapper {
    /**
     * Records [event] with optional [properties].
     *
     * Implementations must never throw: analytics is best-effort telemetry and must not be able to
     * break a user flow.
     */
    fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>)
}
