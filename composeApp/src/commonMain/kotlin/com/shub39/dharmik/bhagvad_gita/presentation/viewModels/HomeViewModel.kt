package com.shub39.dharmik.bhagvad_gita.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        }
    }

    private fun observeJob() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
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