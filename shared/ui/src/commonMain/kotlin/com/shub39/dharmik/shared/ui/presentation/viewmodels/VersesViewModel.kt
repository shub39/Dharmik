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
import com.shub39.dharmik.shared.core.domain.AudioRepository
import com.shub39.dharmik.shared.core.domain.BgRepo
import com.shub39.dharmik.shared.core.domain.LongPair
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.core.domain.audioVariant
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.MediaSource
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
 */
@KoinViewModel
class VersesViewModel(
    @InjectedParam private val route: Verses,
    @Provided private val repo: BgRepo,
    @Provided private val datastore: PreferencesRepo,
    @Provided private val player: MediaPlayer,
    @Provided private val audioRepository: AudioRepository,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(VersesState())

    val state =
        _state
            .asStateFlow()
            .onStart { observeState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        viewModelScope.launch { loadRouteChapter() }
    }

    fun onAction(action: VersesAction) {
        viewModelScope.launch {
            when (action) {
                is VersesAction.SetFave -> {
                    if (_state.value.favorites.contains(action.verse)) {
                        repo.deleteFave(action.verse)
                    } else {
                        repo.setFave(action.verse)
                    }
                }

                is VersesAction.SetIndex -> {
                    datastore.setBgBookMark(action.mark)
                }

                is VersesAction.SetVerseCardState -> {
                    datastore.setVerseCardState(action.state)
                    player.pause()

                    _state.update { it.copy(verseCardState = action.state, isPlaying = false) }
                }

                is VersesAction.SetAutoPlay -> {
                    _state.update { it.copy(autoPlay = action.autoPlay) }
                }

                VersesAction.Pause -> {
                    player.pause()
                    _state.update { it.copy(isPlaying = false) }
                }

                is VersesAction.ChangeVerse -> {
                    action.coroutineScope.launch {
                        _state.value.pagerState.animateScrollToPage(action.index)
                        player.pause()

                        _state.update { it.copy(isPlaying = false) }

                        if (_state.value.autoPlay) {
                            player.setSource(
                                audioSourceFor(_state.value, action.index, audioRepository)
                            )
                            player.play()

                            _state.update { it.copy(isPlaying = true) }
                        }

                        if (route.saveBookmarks) {
                            datastore.setBgBookMark(LongPair(route.chapter, action.index.toLong()))
                        }
                    }
                }

                is VersesAction.Play -> {
                    _state.update { it.copy(isPreparing = true) }
                    player.setSource(audioSourceFor(_state.value, action.index, audioRepository))
                    player.play()
                    _state.update { it.copy(isPlaying = true, isPreparing = false) }
                }
            }
        }
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
                player = player,
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
            }
        }
}

/**
 * Resolves the playable source for [index], downloading the track from the legacy Gita Supersite
 * host first when it is not already stored locally.
 */
private suspend fun audioSourceFor(
    state: VersesState,
    index: Int,
    repository: AudioRepository,
): MediaSource {
    val verse = state.currentVerses.getOrNull(index) ?: state.currentVerses.first()
    val uri = repository.resolve(verse.chapter, verse.verse, state.verseCardState.audioVariant)

    return MediaSource.Url(uri)
}
