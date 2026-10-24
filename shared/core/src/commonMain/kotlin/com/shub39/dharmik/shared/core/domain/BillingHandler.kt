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

import kotlinx.coroutines.flow.StateFlow

/**
 * Read-only view of the user's subscription state.
 *
 * This is the only seam between the shared modules and an actual billing backend: the
 * implementations live in the platform apps, so the shared code never depends on a purchase SDK.
 *
 * Implementations must never throw from [userResult]: billing is a network operation that can fail
 * for reasons outside the app's control, and a failure must degrade to "not subscribed" instead of
 * breaking a user flow.
 */
interface BillingHandler {
    /** Whether the paid entitlement is currently active. Updated by the platform implementation. */
    val isPlus: StateFlow<Boolean>

    /** Refreshes [isPlus] and returns its new value. */
    suspend fun isPlusUser(): Boolean

    /** Fetches the current subscription state. */
    suspend fun userResult(): SubscriptionResult

    /** Whether this build is the FOSS variant, which has no billing support at all. */
    suspend fun isFoss(): Boolean
}
