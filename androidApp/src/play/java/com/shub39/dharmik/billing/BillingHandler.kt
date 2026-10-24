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

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.shub39.dharmik.shared.core.DharmikLogger
import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.SubscriptionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

/**
 * RevenueCat backed subscription state. Only compiled into the `play` flavor.
 *
 * The SDK instance is resolved lazily and may be `null` while the API key is still a placeholder,
 * in which case the user is reported as not subscribed instead of the app crashing.
 */
@Single
class BillingHandler : BillingHandler {
    private val _isPlus = MutableStateFlow(false)
    override val isPlus: StateFlow<Boolean> = _isPlus

    private val purchases: Purchases? by lazy {
        runCatching { Purchases.sharedInstance }
            .onFailure { DharmikLogger.d(TAG, "RevenueCat is not configured, billing is disabled") }
            .getOrNull()
    }

    init {
        purchases?.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
            _isPlus.update { customerInfo.entitlements.all[ENTITLEMENT_PLUS]?.isActive == true }
        }
    }

    override suspend fun isPlusUser(): Boolean {
        _isPlus.update { userResult() is SubscriptionResult.Subscribed }
        return _isPlus.value
    }

    override suspend fun userResult(): SubscriptionResult {
        val client = purchases ?: return SubscriptionResult.NotSubscribed

        return try {
            val customerInfo =
                withContext(Dispatchers.IO) {
                    client.awaitCustomerInfo(
                        fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT
                    )
                }

            val isSubscribed = customerInfo.entitlements.all[ENTITLEMENT_PLUS]?.isActive == true
            _isPlus.update { isSubscribed }

            if (isSubscribed) SubscriptionResult.Subscribed else SubscriptionResult.NotSubscribed
        } catch (e: Exception) {
            DharmikLogger.e(TAG, "Failed to fetch customer info", e)
            SubscriptionResult.Error(e)
        }
    }

    override suspend fun isFoss(): Boolean = false

    private companion object {
        private const val TAG = "BillingHandler"
        private const val ENTITLEMENT_PLUS = "Plus"
    }
}
