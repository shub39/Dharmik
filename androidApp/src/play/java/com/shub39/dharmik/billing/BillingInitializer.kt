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

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.shub39.dharmik.BuildConfig
import com.shub39.dharmik.shared.core.DharmikLogger

/** Sets up the RevenueCat client. Only compiled into the `play` flavor. */
class BillingInitializer {
    fun initialize(context: Context) {
        try {
            Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
            Purchases.configure(
                PurchasesConfiguration.Builder(
                        context,
                        if (BuildConfig.DEBUG) TEST_KEY else PURCHASES_KEY,
                    )
                    .build()
            )
        } catch (e: Exception) {
            // A misconfigured key must not prevent the app from starting.
            DharmikLogger.e(TAG, "Failed to configure RevenueCat", e)
        }
    }

    private companion object {
        private const val TAG = "BillingInitializer"

        private const val TEST_KEY = "test_OlEeRGWxRuVVWZPMWQUAOymWmYA"
        private const val PURCHASES_KEY = "goog_tgiQthJoyXwpdYdihSQGptXFjCO"
    }
}
