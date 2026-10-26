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
import com.shub39.dharmik.shared.core.domain.BgRepo
import com.shub39.dharmik.shared.core.domain.CollectionsRepo
import com.shub39.dharmik.shared.core.domain.LongPair
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.ui.navigation.AddToCollection
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionEditor
import com.shub39.dharmik.shared.ui.presentation.collections.add.AddToCollectionAction
import com.shub39.dharmik.shared.ui.presentation.collections.add.AddToCollectionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/**
 * Collects the verse carried by the route into the selected collections.
 *
 * A new collection can be created inline from the dialog, which immediately selects it so the
 * collected verse lands in it on save.
 */
@KoinViewModel
class AddToCollectionViewModel(
    @InjectedParam private val route: AddToCollection,
    @Provided private val repo: CollectionsRepo,
    @Provided private val bgRepo: BgRepo,
    @Provided private val datastore: PreferencesRepo,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(AddToCollectionState())

    val state =
        _state
            .asStateFlow()
            .onStart { observeState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        viewModelScope.launch { loadVerse() }
    }

    fun onAction(action: AddToCollectionAction) {
        when (action) {
            is AddToCollectionAction.ToggleCollection ->
                _state.update {
                    it.copy(
                        selectedIds =
                            if (action.id in it.selectedIds) it.selectedIds - action.id
                            else it.selectedIds + action.id
                    )
                }

            AddToCollectionAction.OpenEditor ->
                _state.update { it.copy(editor = CollectionEditor()) }

            is AddToCollectionAction.EditName ->
                _state.update { it.copy(editor = it.editor?.copy(name = action.name)) }

            is AddToCollectionAction.EditDescription ->
                _state.update {
                    it.copy(editor = it.editor?.copy(description = action.description))
                }

            AddToCollectionAction.DismissEditor -> _state.update { it.copy(editor = null) }

            AddToCollectionAction.SaveEditor -> viewModelScope.launch { saveEditor() }

            is AddToCollectionAction.SetNote -> _state.update { it.copy(note = action.note) }

            AddToCollectionAction.Save -> viewModelScope.launch { save() }
        }
    }

    private suspend fun loadVerse() {
        val verses =
            runCatching { bgRepo.getChapter(route.chapter.toInt()).gitaVerses }
                .getOrDefault(emptyList())

        _state.update {
            it.copy(
                verse = verses.firstOrNull { verse -> verse.verse == route.verse },
                verseCardState = datastore.getVerseCardState().first(),
                fontSize = datastore.getFontSize().first(),
            )
        }
    }

    private suspend fun saveEditor() {
        val editor = _state.value.editor ?: return
        val name = editor.name.trim()
        if (name.isEmpty()) return

        val id = repo.createCollection(name, editor.description)
        analytics.trackEvent(AnalyticsEvent.COLLECTION_CREATED, mapOf("collection_id" to id))

        _state.update { it.copy(editor = null, selectedIds = it.selectedIds + id) }
    }

    private suspend fun save() {
        val current = _state.value.verse ?: return
        _state.update { it.copy(isSaving = true) }

        val targets = _state.value.selectedIds.toMutableSet()
        val note = _state.value.note.trim()

        targets.forEach { collectionId ->
            val existing =
                _state.value.collections
                    .firstOrNull { it.id == collectionId }
                    ?.verses
                    ?.firstOrNull { it.verse == LongPair(current.chapter, current.verse) }

            if (existing == null) {
                repo.addVerse(collectionId, current.chapter, current.verse, note)
                analytics.trackEvent(
                    AnalyticsEvent.VERSE_ADDED_TO_COLLECTION,
                    mapOf(
                        "chapter" to current.chapter,
                        "verse" to current.verse,
                        "collection_id" to collectionId,
                    ),
                )
            } else if (note.isNotEmpty() && existing.notes != note) {
                repo.updateVerseNotes(existing.id, note)
                analytics.trackEvent(
                    AnalyticsEvent.VERSE_NOTE_UPDATED,
                    mapOf("collection_id" to collectionId),
                )
            }
        }

        _state.update { it.copy(isSaving = false, isSaved = true) }
    }

    private fun observeState() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                repo
                    .getCollectionsFlow()
                    .onEach { collections ->
                        val current = _state.value.verse

                        // Pre-tick the collections that already hold this verse.
                        val preselected =
                            if (current == null) emptySet()
                            else
                                collections
                                    .filter { collection ->
                                        collection.verses.any {
                                            it.verse == LongPair(current.chapter, current.verse)
                                        }
                                    }
                                    .map { it.id }
                                    .toSet()

                        _state.update {
                            it.copy(
                                collections = collections,
                                selectedIds = it.selectedIds + preselected,
                            )
                        }
                    }
                    .launchIn(this)
            }
        }
}
