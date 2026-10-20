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
import com.shub39.dharmik.shared.core.domain.AudioBulkRepo
import com.shub39.dharmik.shared.core.domain.ChangelogRepo
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.ui.presentation.settings.SettingsAction
import com.shub39.dharmik.shared.ui.presentation.settings.SettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SettingsViewModel(
    @Provided private val datastore: PreferencesRepo,
    @Provided private val audioRepo: AudioBulkRepo,
    @Provided private val changelogRepo: ChangelogRepo,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(SettingsState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                analytics.trackEvent(AnalyticsEvent.SETTINGS_VIEWED, emptyMap())
                observeState()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun onAction(action: SettingsAction) =
        viewModelScope.launch {
            when (action) {
                is SettingsAction.OnSetAppTheme -> {
                    datastore.setAppTheme(action.appTheme)
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("theme" to action.appTheme.name),
                    )
                }

                is SettingsAction.OnSetVerseCardState -> datastore.setVerseCardState(action.state)

                is SettingsAction.SetFontSize -> {
                    datastore.setFontSize(action.fontSize)
                    analytics.trackEvent(
                        AnalyticsEvent.FONT_SIZE_CHANGED,
                        // Rounded so that dragging the slider does not explode the property
                        // cardinality.
                        mapOf("size" to (action.fontSize * 10).toInt() / 10f),
                    )
                }

                SettingsAction.OnDownloadAudio -> {
                    analytics.trackEvent(AnalyticsEvent.BULK_AUDIO_DOWNLOAD_STARTED, emptyMap())
                    audioRepo.start()
                }

                SettingsAction.OnCancelAudioDownload -> {
                    analytics.trackEvent(AnalyticsEvent.BULK_AUDIO_DOWNLOAD_CANCELLED, emptyMap())
                    audioRepo.cancel()
                }

                SettingsAction.OnDeleteAudio -> {
                    analytics.trackEvent(AnalyticsEvent.BULK_AUDIO_DELETED, emptyMap())
                    audioRepo.delete()
                }
            }
        }

    /** Tracked when the About screen is opened. */
    fun trackAboutViewed() {
        analytics.trackEvent(AnalyticsEvent.ABOUT_VIEWED, emptyMap())
    }

    /** Tracked when the Changelog screen is opened. */
    fun trackChangelogViewed() {
        analytics.trackEvent(AnalyticsEvent.CHANGELOG_VIEWED, emptyMap())
    }

    private fun observeState() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                datastore
                    .getFontSize()
                    .onEach { size -> _state.update { it.copy(fontSize = size) } }
                    .launchIn(this)

                datastore
                    .getVerseCardState()
                    .onEach { verseCardState ->
                        _state.update { it.copy(verseCardState = verseCardState) }
                    }
                    .launchIn(this)

                datastore
                    .getAppTheme()
                    .onEach { theme ->
                        _state.update { it.copy(theme = it.theme.copy(appTheme = theme)) }
                    }
                    .launchIn(this)

                audioRepo.state
                    .onEach { bulk -> _state.update { it.copy(bulkAudio = bulk) } }
                    .launchIn(this)

                changelogRepo.changelog
                    .onEach { changelog ->
                        _state.update {
                            it.copy(
                                changelog = changelog,
                                currentVersion = changelog.firstOrNull()?.version,
                            )
                        }
                    }
                    .launchIn(this)

                launch { audioRepo.refresh() }
            }
        }
}
