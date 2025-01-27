package com.shub39.dharmik.atharva_veda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.atharva_veda.domain.AvKaandasRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AvViewModel(
    private val avKaandasRepo: AvKaandasRepo
): ViewModel() {
    private val _kaandas = MutableStateFlow(AvState())
    val kaandas = _kaandas.asStateFlow()
        .onStart {
            _kaandas.update {
                it.copy(
                    kaandas = avKaandasRepo.getKaandas()
                )
            }
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
                           currentKaandas = action.kaandas
                       )
                   }
               }
           }
        }
    }
}