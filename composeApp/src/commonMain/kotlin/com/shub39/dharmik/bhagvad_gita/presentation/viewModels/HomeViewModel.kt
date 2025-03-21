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
                val audios = repo.getAudios(_state.value.currentBookMark.first.toInt())

                stateLayer.versesState.update {
                    it.copy(
                        currentVerses = file.gitaVerses,
                        saveBookMarks = true,
                        audioFiles = audios,
                        pagerState = PagerState(_state.value.currentBookMark.second.toInt().coerceAtLeast(0)) { file.gitaVerses.size }
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
                val file = repo.getChapter(action.index)
                val audios = repo.getAudios(action.index)

                stateLayer.versesState.update {
                    it.copy(
                        currentVerses = file.gitaVerses,
                        audioFiles = audios,
                        saveBookMarks = true,
                        pagerState = PagerState { file.gitaVerses.size }
                    )
                }
            }

            is HomeAction.LoadVerse -> {
                val file = repo.getChapter(action.verse.chapter.toInt())
                val audios = repo.getAudios(action.verse.chapter.toInt())

                stateLayer.versesState.update {
                    it.copy(
                        currentVerses = file.gitaVerses,
                        audioFiles = audios,
                        saveBookMarks = false,
                        pagerState = PagerState(action.verse.verse.toInt().minus(1).coerceAtLeast(0)) { file.gitaVerses.size }
                    )
                }
            }
        }
    }

    private fun observeJob() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
            repo.getFavesFlow()
                .onEach { faves ->
                    _state.update {
                        it.copy(
                            favorites = faves
                        )
                    }

                    stateLayer.versesState.update {
                        it.copy(
                            favorites = faves
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