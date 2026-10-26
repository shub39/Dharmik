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
package com.shub39.dharmik.billing

import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.SubscriptionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

/**
 * The `foss` flavor has no billing backend, so everything it could sell is simply considered
 * unlocked.
 */
@Single
class BillingHandler : BillingHandler {
    override val isPlus: StateFlow<Boolean> = MutableStateFlow(true)

    override suspend fun isPlusUser(): Boolean = true

    override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed

    override suspend fun isFoss(): Boolean = true
}
