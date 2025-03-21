package com.shub39.dharmik.bhagvad_gita.presentation.viewModels

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.presentation.verses.VersesAction
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

class VersesViewModel(
    private val stateLayer: StateLayer,
    private val repo: BgRepo,
    private val datastore: PreferencesRepo
) : ViewModel() {
    private var observeFavesJob: Job? = null

    private val _state = stateLayer.versesState

    val state = _state.asStateFlow()
        .onStart {
            _state.update {
                it.copy(
                    currentFile = repo.getChapter(1).gitaVerse
                )
            }

            observeFaves()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: VersesAction) {
        viewModelScope.launch {
            when (action) {
                is VersesAction.ChapterChange -> {
                    val file = repo.getChapter(action.index)

                    _state.update {
                        it.copy(
                            currentFile = file.gitaVerse,
                            pagerState = PagerState { file.gitaVerse.size }
                        )
                    }
                }

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

                VersesAction.LoadBookMark -> {
                    val file = repo.getChapter(_state.value.currentBookMark.first.toInt())

                    _state.update {
                        it.copy(
                            currentFile = file.gitaVerse,
                            pagerState = PagerState(it.currentBookMark.second.toInt()) { file.gitaVerse.size }
                        )
                    }
                }

                is VersesAction.SetVerses -> {
                    _state.update {
                        it.copy(
                            currentFile = action.verses,
                            pagerState = PagerState { action.verses.size }
                        )
                    }
                }
            }
        }
    }

    private fun observeFaves() = viewModelScope.launch {
        observeFavesJob?.cancel()
        observeFavesJob = launch {
            repo.getFavesFlow()
                .onEach { flow ->
                    _state.update {
                        it.copy(
                            favorites = flow
                        )
                    }
                }
                .launchIn(this)

            datastore.getBgBookMark()
                .onEach { mark ->
                    _state.update {
                        it.copy(
                            currentBookMark = mark
                        )
                    }
                }
                .launchIn(this)
        }
    }
}