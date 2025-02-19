package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.atharva_veda.domain.AvKaandasRepo
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

class AvViewModel(
    private val avKaandasRepo: AvKaandasRepo,
    private val datastore: PreferencesRepo
): ViewModel() {
    private var faveObserve: Job? = null

    private val _kaandas = MutableStateFlow(AvState())
    val kaandas = _kaandas.asStateFlow()
        .onStart {
            _kaandas.update {
                it.copy(
                    kaandas = avKaandasRepo.getKaandas()
                )
            }

            observeFaves()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _kaandas.value
        )

    fun onAction(action: AvAction) {
        viewModelScope.launch {
           when (action) {
               is AvAction.SetKaandas -> {
                   _kaandas.update {
                       it.copy(
                           currentKaandas = action.kaandas,
                           pagerState = PagerState { action.kaandas.size }
                       )
                   }
               }

               is AvAction.SetFave -> {
                   if (_kaandas.value.favorites.contains(action.verse)) {
                       avKaandasRepo.deleteFave(action.verse)
                   } else {
                       avKaandasRepo.setFave(action.verse)
                   }
               }

               AvAction.LoadBookMark -> {
                   val kaandas = _kaandas.value.kaandas[_kaandas.value.currentBookMark.first] ?: emptyList()

                   _kaandas.update {
                       it.copy(
                           currentKaandas = kaandas,
                           pagerState = PagerState(it.currentBookMark.second) { kaandas.size }
                       )
                   }
               }

               is AvAction.SetBookMark -> {
                   datastore.setAvBookMark(action.mark)
               }
           }
        }
    }

    private fun observeFaves() = viewModelScope.launch {
        faveObserve?.cancel()
        faveObserve = launch {
            avKaandasRepo.getFavesFlow()
                .onEach { list ->
                    _kaandas.update {
                        it.copy(
                            favorites = list
                        )
                    }
                }
                .launchIn(this)

            datastore.getAvBookMark()
                .onEach { mark ->
                    _kaandas.update {
                        it.copy(
                            currentBookMark = mark
                        )
                    }
                }
                .launchIn(this)
        }
    }
}