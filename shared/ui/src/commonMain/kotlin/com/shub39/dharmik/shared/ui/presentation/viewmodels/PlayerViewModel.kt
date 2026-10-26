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
import com.shub39.dharmik.shared.core.domain.AudioTrack
import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.BgRepo
import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.CollectionsRepo
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.core.domain.PlaybackController
import com.shub39.dharmik.shared.core.domain.PlaybackState
import com.shub39.dharmik.shared.core.domain.Playlist
import com.shub39.dharmik.shared.core.domain.PlaylistSource
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.core.domain.audioVariant
import com.shub39.dharmik.shared.core.domain.chapterPlaylist
import com.shub39.dharmik.shared.ui.navigation.Player
import com.shub39.dharmik.shared.ui.presentation.player.PlayerAction
import com.shub39.dharmik.shared.ui.presentation.player.PlayerState
import com.shub39.dharmik.shared.ui.presentation.removeExtraLineBreaks
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
 * Backs the player screen.
 *
 * The session itself lives in [PlaybackController], so this view model only starts a new session
 * when the route asks for one and otherwise mirrors whatever is playing. That is what lets the
 * Player tab resume the current session after navigating away.
 */
@KoinViewModel
class PlayerViewModel(
    @InjectedParam private val route: Player,
    @Provided private val controller: PlaybackController,
    @Provided private val collectionsRepo: CollectionsRepo,
    @Provided private val bgRepo: BgRepo,
    @Provided private val datastore: PreferencesRepo,
    @Provided private val billing: BillingHandler,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null
    private val chapterCache = mutableMapOf<Long, List<GitaVerse>>()

    private val _state = MutableStateFlow(PlayerState())

    val state =
        _state
            .asStateFlow()
            .onStart { observeState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        analytics.trackEvent(
            AnalyticsEvent.PLAYER_VIEWED,
            mapOf(
                "source" to
                    when {
                        route.chapter != null -> "chapter"
                        route.collectionId != null -> "collection"
                        else -> "session"
                    }
            ),
        )

        viewModelScope.launch { startFromRoute() }
    }

    fun onAction(action: PlayerAction) {
        when (action) {
            PlayerAction.PlayPause -> viewModelScope.launch { controller.playPause() }

            PlayerAction.Next -> viewModelScope.launch { controller.next() }

            PlayerAction.Previous -> viewModelScope.launch { controller.previous() }

            is PlayerAction.SeekTo ->
                viewModelScope.launch { controller.seekTo(action.positionMillis) }

            is PlayerAction.SelectTrack -> {
                _state.update { it.copy(queueSheetOpen = false) }
                viewModelScope.launch { controller.select(action.index) }
            }

            is PlayerAction.SetVariant ->
                viewModelScope.launch { controller.setVariant(action.variant) }

            PlayerAction.Retry ->
                viewModelScope.launch { controller.select(_state.value.playback.index) }

            PlayerAction.OpenQueue -> {
                analytics.trackEvent(AnalyticsEvent.PLAYLIST_VIEWED, emptyMap())
                _state.update { it.copy(queueSheetOpen = true) }
            }

            PlayerAction.DismissQueue -> _state.update { it.copy(queueSheetOpen = false) }
        }
    }

    /** Starts the session the route asks for; a bare `Player()` keeps whatever is playing. */
    private suspend fun startFromRoute() {
        val variant = datastore.getVerseCardState().first().audioVariant

        when {
            route.chapter != null -> controller.play(chapterPlaylist(route.chapter), variant)

            route.collectionId != null -> {
                val collection =
                    collectionsRepo.getCollectionFlow(route.collectionId).first() ?: return
                val playlist =
                    Playlist(
                        source = PlaylistSource.Collection(collection.id, collection.name),
                        tracks =
                            collection.verses.map {
                                AudioTrack(chapter = it.verse.first, verse = it.verse.second)
                            },
                    )

                if (playlist.tracks.isEmpty()) return

                controller.play(playlist, variant)
            }

            else -> Unit
        }
    }

    private fun observeState() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                controller.state
                    .onEach { playback ->
                        val previousTrack = _state.value.playback.currentTrack

                        _state.update { it.copy(playback = playback) }

                        if (playback.currentTrack != previousTrack) {
                            playback.currentTrack?.let { track ->
                                analytics.trackEvent(
                                    AnalyticsEvent.AUDIO_TRACK_CHANGED,
                                    mapOf("chapter" to track.chapter, "verse" to track.verse),
                                )
                            }

                            loadVerseText(playback)
                        }
                    }
                    .launchIn(this)

                // Listening to playlists is a Plus feature, so the screen needs the entitlement.
                billing.isPlus
                    .onEach { isPlus -> _state.update { it.copy(isPlus = isPlus) } }
                    .launchIn(this)
            }
        }

    /** Resolves the verse that is playing, in the language of the audio variant. */
    private suspend fun loadVerseText(playback: PlaybackState) {
        val track = playback.currentTrack
        if (track == null) {
            _state.update { it.copy(verseText = "") }
            return
        }

        val verseCardState = playback.variant.verseCardState
        val verse = chapterVerses(track.chapter).firstOrNull { it.verse == track.verse }
        val text =
            when (verseCardState) {
                    VerseCardState.SANSKRIT -> verse?.text
                    VerseCardState.HINDI -> verse?.translations?.swamiTejomayananda
                    VerseCardState.ENGLISH -> verse?.translations?.shriPurohitSwami
                }
                .orEmpty()
                .removeExtraLineBreaks()

        _state.update { it.copy(verseCardState = verseCardState, verseText = text) }
    }

    private suspend fun chapterVerses(chapter: Long): List<GitaVerse> {
        chapterCache[chapter]?.let {
            return it
        }

        val verses =
            runCatching { bgRepo.getChapter(chapter.toInt()).gitaVerses }.getOrDefault(emptyList())

        chapterCache[chapter] = verses

        return verses
    }
}

/** The verse card language that matches the audio variant that is playing. */
private val AudioVariant.verseCardState: VerseCardState
    get() =
        when (this) {
            AudioVariant.MOOL -> VerseCardState.SANSKRIT
            AudioVariant.HINDI -> VerseCardState.HINDI
            AudioVariant.ENGLISH -> VerseCardState.ENGLISH
        }
