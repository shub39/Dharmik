/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.ui.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.dharmik.shared.core.AnalyticsEvent
import com.shub39.dharmik.shared.core.domain.AnalyticsWrapper
import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.CollectionsRepo
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionEditor
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionsAction
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionsState
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
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class CollectionsViewModel(
    @Provided private val repo: CollectionsRepo,
    @Provided private val billing: BillingHandler,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(CollectionsState())

    val state =
        _state
            .asStateFlow()
            .onStart {
                analytics.trackEvent(AnalyticsEvent.COLLECTIONS_VIEWED, emptyMap())
                observeState()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun onAction(action: CollectionsAction) {
        when (action) {
            CollectionsAction.OpenEditor -> _state.update { it.copy(editor = CollectionEditor()) }

            is CollectionsAction.OpenEditorFor ->
                _state.update {
                    it.copy(
                        editor =
                            CollectionEditor(
                                id = action.collection.id,
                                name = action.collection.name,
                                description = action.collection.description,
                            )
                    )
                }

            is CollectionsAction.EditName ->
                _state.update { it.copy(editor = it.editor?.copy(name = action.name)) }

            is CollectionsAction.EditDescription ->
                _state.update {
                    it.copy(editor = it.editor?.copy(description = action.description))
                }

            CollectionsAction.DismissEditor -> _state.update { it.copy(editor = null) }

            CollectionsAction.SaveEditor -> viewModelScope.launch { saveEditor() }
        }
    }

    private suspend fun saveEditor() {
        val editor = _state.value.editor ?: return
        val name = editor.name.trim()
        if (name.isEmpty()) return

        if (editor.id == null) {
            val id = repo.createCollection(name, editor.description)
            analytics.trackEvent(AnalyticsEvent.COLLECTION_CREATED, mapOf("collection_id" to id))
        } else {
            repo.updateCollection(editor.id, name, editor.description)
            analytics.trackEvent(
                AnalyticsEvent.COLLECTION_UPDATED,
                mapOf("collection_id" to editor.id),
            )
        }

        _state.update { it.copy(editor = null) }
    }

    private fun observeState() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                repo
                    .getCollectionsFlow()
                    .onEach { collections -> _state.update { it.copy(collections = collections) } }
                    .launchIn(this)

                // Writing is a Plus feature, so the screen needs the current entitlement.
                billing.isPlus
                    .onEach { isPlus -> _state.update { it.copy(isPlus = isPlus) } }
                    .launchIn(this)
            }
        }
}
