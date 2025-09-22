package com.shub39.dharmik.bhagvad_gita.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.presentation.verses.VersesAction
import com.shub39.dharmik.core.domain.LongPair
import com.shub39.dharmik.core.domain.PreferencesRepo
import com.shub39.dharmik.core.domain.VerseCardState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VersesViewModel(
    private val stateLayer: StateLayer,
    private val repo: BgRepo,
    private val datastore: PreferencesRepo
) : ViewModel() {

    private val _state = stateLayer.versesState

    val state = _state.asStateFlow()
        .onStart {
            _state.update {
                it.copy(
                    currentVerses = repo.getChapter(1).gitaVerses,
                    verseCardState = stateLayer.homeState.value.verseCardState
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            _state.value
        )

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
                    _state.update {
                        it.copy(
                            verseCardState = action.state,
                            isPlaying = false
                        )
                    }

                    _state.value.playerHost.pause()
                }

                is VersesAction.SetAutoPlay -> {
                    _state.update {
                        it.copy(
                            autoPlay = action.autoPlay
                        )
                    }
                }

                VersesAction.Pause -> {
                    _state.value.playerHost.pause()
                    _state.update {
                        it.copy(
                            isPlaying = false
                        )
                    }
                }

                is VersesAction.ChangeVerse -> {
                    action.coroutineScope.launch {
                        _state.value.pagerState.animateScrollToPage(action.index)
                        _state.value.playerHost.pause()

                        _state.update {
                            it.copy(
                                isPlaying = false
                            )
                        }

                        if (_state.value.autoPlay) {
                            val audios = _state.value.audioFiles[action.index]

                            _state.value.playerHost.loadUrl(
                                when (_state.value.verseCardState) {
                                    VerseCardState.ENGLISH -> audios.englishTranslation
                                    VerseCardState.HINDI -> audios.hindiTranslation
                                    VerseCardState.SANSKRIT -> audios.moolSloka
                                }
                            )
                            _state.value.playerHost.play()

                            _state.update {
                                it.copy(
                                    isPlaying = true
                                )
                            }
                        }

                        if (_state.value.saveBookMarks) {
                            datastore.setBgBookMark(
                                LongPair(
                                    _state.value.currentVerses.first().chapter,
                                    action.index.toLong()
                                )
                            )
                        }
                    }
                }

                is VersesAction.Play -> {
                    val audios = _state.value.audioFiles[action.index]

                    _state.value.playerHost.loadUrl(
                        when (_state.value.verseCardState) {
                            VerseCardState.ENGLISH -> audios.englishTranslation
                            VerseCardState.HINDI -> audios.hindiTranslation
                            VerseCardState.SANSKRIT -> audios.moolSloka
                        }
                    )
                    _state.value.playerHost.play()
                    _state.update {
                        it.copy(
                            isPlaying = true
                        )
                    }
                }
            }
        }
    }
}