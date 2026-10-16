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
import com.shub39.dharmik.shared.core.domain.Audios
import com.shub39.dharmik.shared.core.domain.BgRepo
import com.shub39.dharmik.shared.core.domain.LongPair
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.core.media.MediaPlayer
import com.shub39.dharmik.shared.core.media.MediaSource
import com.shub39.dharmik.shared.ui.presentation.verses.VersesAction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class VersesViewModel(
    @Provided private val stateLayer: StateLayer,
    @Provided private val repo: BgRepo,
    @Provided private val datastore: PreferencesRepo,
    @Provided private val player: MediaPlayer,
) : ViewModel() {

    private val _state = stateLayer.versesState

    val state =
        _state
            .asStateFlow()
            .onStart {
                _state.update {
                    it.copy(
                        currentVerses = repo.getChapter(1).gitaVerses,
                        verseCardState = stateLayer.homeState.value.verseCardState,
                        player = player,
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun onAction(action: VersesAction) {
        viewModelScope.launch {
            when (action) {
                is VersesAction.SetFave -> {
                    if (stateLayer.homeState.value.favorites.contains(action.verse)) {
                        repo.deleteFave(action.verse)
                    } else {
                        repo.setFave(action.verse)
                    }
                }

                is VersesAction.SetIndex -> {
                    datastore.setBgBookMark(action.mark)
                }

                is VersesAction.SetVerseCardState -> {
                    _state.update { it.copy(verseCardState = action.state, isPlaying = false) }

                    player.pause()
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
                            player.setSource(_state.value.audioSourceFor(action.index))
                            player.play()

                            _state.update { it.copy(isPlaying = true) }
                        }

                        if (_state.value.saveBookMarks) {
                            datastore.setBgBookMark(
                                LongPair(
                                    _state.value.currentVerses.first().chapter,
                                    action.index.toLong(),
                                )
                            )
                        }
                    }
                }

                is VersesAction.Play -> {
                    player.setSource(_state.value.audioSourceFor(action.index))
                    player.play()
                    _state.update { it.copy(isPlaying = true) }
                }
            }
        }
    }
}

/**
 * Resolves the audio URL for [index] in the currently selected language.
 *
 * Playback itself is stubbed out for now - audio is not bundled with the app and will become a user
 * initiated download later on.
 */
private fun com.shub39.dharmik.shared.ui.presentation.verses.VersesState.audioSourceFor(
    index: Int
): MediaSource {
    val audios: Audios = audioFiles[index]

    return MediaSource.Url(
        when (verseCardState) {
            VerseCardState.ENGLISH -> audios.englishTranslation
            VerseCardState.HINDI -> audios.hindiTranslation
            VerseCardState.SANSKRIT -> audios.moolSloka
        }
    )
}
