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

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.core.domain.BgRepo
import com.shub39.dharmik.shared.core.domain.LongPair
import com.shub39.dharmik.shared.core.domain.PlaybackController
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.core.domain.audioVariant
import com.shub39.dharmik.shared.core.domain.chapterPlaylist
import com.shub39.dharmik.shared.ui.navigation.Verses
import com.shub39.dharmik.shared.ui.presentation.verses.VersesAction
import com.shub39.dharmik.shared.ui.presentation.verses.VersesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/**
 * Backs the reading screen. Everything it shows is derived from the navigation [route] and the
 * shared repositories, which it observes on its own.
 *
 * Playback is delegated to the app-wide [PlaybackController]: the reader only decides *what* to
 * queue, so audio started here keeps playing (and keeps a media notification) when the screen goes
 * away.
 */
@KoinViewModel
class VersesViewModel(
    @InjectedParam private val route: Verses,
    @Provided private val repo: BgRepo,
    @Provided private val datastore: PreferencesRepo,
    @Provided private val controller: PlaybackController,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(VersesState())

    val state =
        _state
            .asStateFlow()
            .onStart { observeState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        analytics.trackEvent(AnalyticsEvent.VERSES_VIEWED, mapOf("chapter" to route.chapter))
        viewModelScope.launch { loadRouteChapter() }
    }

    fun onAction(action: VersesAction) {
        viewModelScope.launch {
            when (action) {
                is VersesAction.SetFave -> {
                    if (_state.value.favorites.contains(action.verse)) {
                        repo.deleteFave(action.verse)
                        analytics.trackEvent(
                            AnalyticsEvent.VERSE_UNFAVORITED,
                            mapOf("chapter" to action.verse.chapter, "verse" to action.verse.verse),
                        )
                    } else {
                        repo.setFave(action.verse)
                        analytics.trackEvent(
                            AnalyticsEvent.VERSE_FAVORITED,
                            mapOf("chapter" to action.verse.chapter, "verse" to action.verse.verse),
                        )
                    }
                }

                is VersesAction.SetIndex -> {
                    datastore.setBgBookMark(action.mark)
                }

                is VersesAction.SetVerseCardState -> {
                    datastore.setVerseCardState(action.state)
                    controller.pause()

                    analytics.trackEvent(
                        AnalyticsEvent.VERSE_CARD_STATE_CHANGED,
                        mapOf("state" to action.state.name),
                    )

                    _state.update { it.copy(verseCardState = action.state) }
                }

                is VersesAction.SetAutoPlay -> {
                    analytics.trackEvent(
                        AnalyticsEvent.AUDIO_AUTOPLAY_CHANGED,
                        mapOf("enabled" to action.autoPlay),
                    )
                    _state.update { it.copy(autoPlay = action.autoPlay) }

                    // Applies to what is already playing too, without losing the position.
                    controller.setAutoAdvance(action.autoPlay)
                }

                VersesAction.Pause -> {
                    controller.pause()
                    analytics.trackEvent(
                        AnalyticsEvent.AUDIO_PAUSED,
                        verseProperties(_state.value.pagerState.currentPage),
                    )
                }

                is VersesAction.ChangeVerse -> {
                    action.coroutineScope.launch {
                        analytics.trackEvent(
                            AnalyticsEvent.VERSE_CHANGED,
                            mapOf("chapter" to route.chapter, "verse" to action.index + 1),
                        )
                        _state.value.pagerState.animateScrollToPage(action.index)

                        // Saved for every page change, including the ones playback drives, so the
                        // bookmark keeps up with what is being listened to.
                        if (route.saveBookmarks) {
                            datastore.setBgBookMark(LongPair(route.chapter, action.index.toLong()))
                        }

                        // The pager also moves when playback advances, so only a real user change
                        // should stop the audio.
                        val playingPage =
                            _state.value.playback.currentTrack?.verse?.toInt()?.minus(1)
                        if (playingPage == action.index) return@launch

                        controller.pause()

                        if (_state.value.autoPlay) {
                            play(index = action.index)
                        }
                    }
                }

                is VersesAction.Play -> {
                    play(index = action.index)
                    analytics.trackEvent(AnalyticsEvent.AUDIO_PLAYED, verseProperties(action.index))
                }
            }
        }
    }

    /** Queues the chapter at [index], so listening can continue past the current verse. */
    private suspend fun play(index: Int) {
        val state = _state.value
        if (state.currentVerses.isEmpty()) return

        controller.play(
            playlist = chapterPlaylist(route.chapter),
            variant = state.verseCardState.audioVariant,
            startIndex = index,
            autoAdvance = state.autoPlay,
        )
    }

    /** Chapter and verse of the verse at [index], for analytics properties. */
    private fun verseProperties(index: Int): Map<String, Any> {
        val verse = _state.value.currentVerses.getOrNull(index)
        return mapOf("chapter" to route.chapter, "verse" to (verse?.verse ?: 0L))
    }

    private suspend fun loadRouteChapter() {
        val verses = repo.getChapter(route.chapter.toInt()).gitaVerses
        val initialPage = route.verse.toInt().coerceIn(0, (verses.size - 1).coerceAtLeast(0))

        _state.update {
            it.copy(
                currentVerses = verses,
                audioFiles = repo.getAudios(route.chapter.toInt()),
                saveBookMarks = route.saveBookmarks,
                verseCardState = datastore.getVerseCardState().first(),
                pagerState = PagerState(initialPage) { verses.size },
            )
        }
    }

    private fun observeState() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                repo
                    .getFavesFlow()
                    .onEach { faves -> _state.update { it.copy(favorites = faves) } }
                    .launchIn(this)

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

                controller.state
                    .onEach { playback -> _state.update { it.copy(playback = playback) } }
                    .launchIn(this)
            }
        }
}
