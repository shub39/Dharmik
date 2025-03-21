package com.shub39.dharmik.bhagvad_gita.presentation.viewModels

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
    private val datastore: PreferencesRepo
): ViewModel() {
    private var observeJob: Job? = null

    private val _state = stateLayer.homeState

    val state = _state.asStateFlow()
        .onStart { observeJob() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: HomeAction) = viewModelScope.launch {
        when (action) {
            is HomeAction.OnSetAppTheme -> datastore.setAppTheme(action.appTheme)

            HomeAction.LoadBookMark -> {
                val file = repo.getChapter(_state.value.currentBookMark.first.toInt())

                stateLayer.versesState.update {
                    it.copy(
                        currentFile = file.gitaVerse,
                        pagerState = PagerState(it.currentBookMark.second.toInt()) { file.gitaVerse.size }
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

            is HomeAction.SetVerses -> {
                stateLayer.versesState.update {
                    it.copy(
                        currentFile = action.verses,
                        pagerState = PagerState { action.verses.size }
                    )
                }
            }

            is HomeAction.ChapterChange -> {
                val file = repo.getChapter(action.index)

                stateLayer.versesState.update {
                    it.copy(
                        currentFile = file.gitaVerse,
                        pagerState = PagerState { file.gitaVerse.size }
                    )
                }
            }

            is HomeAction.LoadVerse -> {
                val file = repo.getChapter(action.verse.chapter.toInt())

                stateLayer.versesState.update {
                    it.copy(
                        currentFile = file.gitaVerse,
                        pagerState = PagerState(action.verse.verse.toInt()) { file.gitaVerse.size }
                    )
                }
            }
        }
    }

    private fun observeJob() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
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
                .onEach { value ->
                    _state.update {
                        it.copy(
                            currentBookMark = value
                        )
                    }
                }
                .launchIn(this)

            datastore.getAppTheme()
                .onEach { theme ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                appTheme = theme
                            )
                        )
                    }
                }
                .launchIn(this)
        }
    }
}