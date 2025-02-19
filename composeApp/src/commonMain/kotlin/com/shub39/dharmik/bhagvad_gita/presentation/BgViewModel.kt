package com.shub39.dharmik.bhagvad_gita.presentation

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.core.domain.PreferencesRepo
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

class BgViewModel(
    private val repo: BgRepo,
    private val datastore: PreferencesRepo
) : ViewModel() {
    private var observeFavesJob: Job? = null

    private val _state = MutableStateFlow(BgState())

    val bgState = _state.asStateFlow()
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
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: BgAction) {
        viewModelScope.launch {
            when (action) {
                is BgAction.ChapterChange -> {
                    val file = repo.getChapter(action.index)

                    _state.update {
                        it.copy(
                            currentFile = file.gitaVerse,
                            pagerState = PagerState { file.gitaVerse.size }
                        )
                    }
                }

                is BgAction.SetFave -> {
                    if (_state.value.favorites.contains(action.verse)) {
                        repo.deleteFave(action.verse)
                    } else {
                        repo.setFave(action.verse)
                    }
                }

                is BgAction.SetIndex -> {
                    datastore.setBgBookMark(action.mark)
                }

                BgAction.LoadBookMark -> {
                    val file = repo.getChapter(_state.value.currentBookMark.first.toInt())

                    _state.update {
                        it.copy(
                            currentFile = file.gitaVerse,
                            pagerState = PagerState(it.currentBookMark.second.toInt()) { file.gitaVerse.size }
                        )
                    }
                }

                is BgAction.SetVerses -> {
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