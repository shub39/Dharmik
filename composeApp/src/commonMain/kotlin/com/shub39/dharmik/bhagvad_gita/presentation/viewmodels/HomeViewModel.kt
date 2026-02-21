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
package com.shub39.dharmik.bhagvad_gita.presentation.viewmodels

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.core.domain.PreferencesRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: BgRepo,
    private val stateLayer: StateLayer,
    private val datastore: PreferencesRepo,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = stateLayer.homeState

    val state =
        _state
            .asStateFlow()
            .onStart { observeJob() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun onAction(action: HomeAction) =
        viewModelScope.launch {
            when (action) {
                is HomeAction.OnSetAppTheme -> datastore.setAppTheme(action.appTheme)

                HomeAction.LoadBookMark -> {
                    val file = repo.getChapter(_state.value.currentBookMark.first.toInt())
                    val audios = repo.getAudios(_state.value.currentBookMark.first.toInt())

                    stateLayer.versesState.update {
                        it.copy(
                            currentVerses = file.gitaVerses,
                            saveBookMarks = true,
                            audioFiles = audios,
                            pagerState =
                                PagerState(
                                    _state.value.currentBookMark.second.toInt().coerceAtLeast(0)
                                ) {
                                    file.gitaVerses.size
                                },
                        )
                    }
                }

                is HomeAction.SetFave -> {
                    if (_state.value.favorites.contains(action.verse)) {
                        repo.deleteFave(action.verse)
                    } else {
                        repo.setFave(action.verse)
                    }
                }

                is HomeAction.ChapterChange -> {
                    println("Chapter changed to ${action.index}")
                    val file = repo.getChapter(action.index)

                    stateLayer.versesState.update {
                        it.copy(
                            currentVerses = file.gitaVerses,
                            audioFiles = repo.getAudios(action.index),
                            saveBookMarks = true,
                            pagerState = PagerState { file.gitaVerses.size },
                        )
                    }
                }

                is HomeAction.LoadVerse -> {
                    val file = repo.getChapter(action.verse.chapter.toInt())

                    stateLayer.versesState.update {
                        it.copy(
                            currentVerses = file.gitaVerses,
                            audioFiles = repo.getAudios(action.verse.chapter.toInt()),
                            saveBookMarks = false,
                            pagerState =
                                PagerState(action.verse.verse.toInt().minus(1).coerceAtLeast(0)) {
                                    file.gitaVerses.size
                                },
                        )
                    }
                }

                is HomeAction.OnSetVerseCardState -> datastore.setVerseCardState(action.state)

                is HomeAction.SetFontSize -> datastore.setFontSize(action.fontSize)
            }
        }

    private fun observeJob() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                datastore
                    .getFontSize()
                    .onEach { size ->
                        _state.update { it.copy(fontSize = size) }

                        stateLayer.versesState.update { it.copy(fontSize = size) }
                    }
                    .launchIn(this)

                repo
                    .getFavesFlow()
                    .onEach { faves ->
                        _state.update { it.copy(favorites = faves) }

                        stateLayer.versesState.update { it.copy(favorites = faves) }
                    }
                    .launchIn(this)

                datastore
                    .getBgBookMark()
                    .onEach { value -> _state.update { it.copy(currentBookMark = value) } }
                    .launchIn(this)

                datastore
                    .getAppTheme()
                    .onEach { theme ->
                        _state.update { it.copy(theme = it.theme.copy(appTheme = theme)) }
                    }
                    .launchIn(this)

                datastore
                    .getVerseCardState()
                    .onEach { state -> _state.update { it.copy(verseCardState = state) } }
                    .launchIn(this)
            }
        }
}
