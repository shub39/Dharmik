package com.shub39.dharmik.bhagvad_gita.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
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
    private val repo: BgRepo
) : ViewModel() {
    private var observeFavesJob: Job? = null

    private val _state = MutableStateFlow(BgState())

    val bgState = _state.asStateFlow()
        .onStart {
            _state.update {
                it.copy(
                    currentFile = repo.getChapter(1)
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
                is BgAction.ChapterChange -> TODO()
                is BgAction.SetFave -> TODO()
            }
        }
    }

    private fun observeFaves() {
        observeFavesJob?.cancel()
        observeFavesJob = repo.getFavesFlow()
            .onEach { flow ->
                _state.update {
                    it.copy(
                        favorites = flow
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}