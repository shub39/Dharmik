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
package com.shub39.dharmik.shared.ui.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.core.domain.SubscriptionResult
import com.shub39.dharmik.shared.ui.app.MainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class MainViewModel(
    @Provided private val datastore: PreferencesRepo,
    @Provided private val analytics: AnalyticsWrapper,
    @Provided private val billing: BillingHandler,
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state =
        _state
            .asStateFlow()
            .onStart {
                analytics.trackEvent(AnalyticsEvent.APP_OPENED, emptyMap())
                observeState()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private fun observeState() =
        viewModelScope.launch {
            datastore
                .getAppTheme()
                .onEach { theme ->
                    _state.update { it.copy(theme = it.theme.copy(appTheme = theme)) }
                }
                .launchIn(this)

            _state.update { it.copy(isFoss = billing.isFoss()) }

            // The entitlement can change while the app stays open (expiry, refund, or a purchase
            // made on another device), so the state follows the flow instead of only being read
            // when the app starts.
            billing.isPlus
                .drop(1) // The current value is resolved by checkSubscription below.
                .onEach { isPlus -> applySubscription(isPlus) }
                .launchIn(this)

            checkSubscription(appStart = true)
        }

    /** Refreshes the subscription state, called after the paywall is dismissed. */
    fun updateSubscription() {
        viewModelScope.launch { checkSubscription() }
    }

    fun trackPaywallOpened() {
        analytics.trackEvent(AnalyticsEvent.PAYWALL_OPENED, emptyMap())
    }

    private suspend fun checkSubscription(appStart: Boolean = false) {
        when (billing.userResult()) {
            SubscriptionResult.Subscribed -> {
                applySubscription(isPlus = true, trackEvent = !appStart)
            }
            is SubscriptionResult.Error,
            SubscriptionResult.NotSubscribed -> {}
        }
    }

    /**
     * Records [isPlus] as the active entitlement. Becoming subscribed while the app is open is
     * reported as a purchase, unless the change comes from the initial fetch at app start.
     */
    private fun applySubscription(isPlus: Boolean, trackEvent: Boolean = true) {
        if (trackEvent && isPlus && !_state.value.isUserSubscribed) {
            analytics.trackEvent(AnalyticsEvent.PAYWALL_PURCHASED, emptyMap())
        }

        _state.update { it.copy(isUserSubscribed = isPlus) }
    }
}
