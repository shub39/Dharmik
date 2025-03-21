package com.shub39.dharmik.bhagvad_gita.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.bhagvad_gita.domain.BgRepo
import com.shub39.dharmik.bhagvad_gita.presentation.verses.VersesAction
import com.shub39.dharmik.core.domain.PreferencesRepo
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
                    currentVerses = repo.getChapter(1).gitaVerses
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
            }
        }
    }
}