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

import com.shub39.dharmik.analytics.AnalyticsImpl
import com.shub39.dharmik.billing.BillingHandler
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.logic.di.LogicModules
import com.shub39.dharmik.shared.ui.di.UIModules
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Configuration
@Module(includes = [UIModules::class, LogicModules::class])
@ComponentScan("com.shub39.dharmik")
class DharmikModules {
    @Single fun provideAnalyticsWrapper(): AnalyticsWrapper = AnalyticsImpl()

    /** Resolves to the RevenueCat implementation in `play` and the no-op one in `foss`. */
    @Single fun provideBillingHandler(): BillingHandler = BillingHandler()
}
