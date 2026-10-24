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
package com.shub39.dharmik.di

import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.DharmikLogger
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.SubscriptionResult
import com.shub39.dharmik.shared.logic.di.LogicModules
import com.shub39.dharmik.shared.ui.di.UIModules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Configuration
@Module(includes = [UIModules::class, LogicModules::class])
@ComponentScan("com.shub39.dharmik")
class DharmikModules {
    /** The desktop harness only logs events; it never talks to an analytics backend. */
    @Single
    fun provideAnalyticsWrapper(): AnalyticsWrapper =
        object : AnalyticsWrapper {
            override fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>) {
                DharmikLogger.d("Analytics", "${event.name}: $properties")
            }
        }

    /** The desktop harness has no billing backend, so everything is considered unlocked. */
    @Single
    fun provideBillingHandler(): BillingHandler =
        object : BillingHandler {
            override val isPlus: StateFlow<Boolean> = MutableStateFlow(true)

            override suspend fun isPlusUser(): Boolean = true

            override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed

            override suspend fun isFoss(): Boolean = true
        }
}
