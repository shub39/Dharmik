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
import com.shub39.dharmik.shared.core.domain.BillingHandler
import com.shub39.dharmik.shared.core.domain.Collections
import com.shub39.dharmik.shared.core.domain.CollectionsRepo
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.core.domain.PreferencesRepo
import com.shub39.dharmik.shared.ui.navigation.CollectionDetail
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionDetailAction
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionDetailState
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionVerseItem
import com.shub39.dharmik.shared.ui.presentation.collections.NoteEditor
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
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/**
 * Backs the collection screen. Collected verses are stored as chapter/verse pairs, so their text is
 * resolved here; chapters are cached because a collection usually spans only a handful of them and
 * every read parses a large JSON file.
 */
@KoinViewModel
class CollectionDetailViewModel(
    @InjectedParam private val route: CollectionDetail,
    @Provided private val repo: CollectionsRepo,
    @Provided private val bgRepo: BgRepo,
    @Provided private val datastore: PreferencesRepo,
    @Provided private val billing: BillingHandler,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var observeJob: Job? = null
    private val chapterCache = mutableMapOf<Long, List<GitaVerse>>()

    private val _state = MutableStateFlow(CollectionDetailState())

    val state =
        _state
            .asStateFlow()
            .onStart { observeState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        analytics.trackEvent(AnalyticsEvent.COLLECTION_VIEWED, mapOf("collection_id" to route.id))
    }

    fun onAction(action: CollectionDetailAction) {
        when (action) {
            is CollectionDetailAction.EditNotes ->
                _state.update {
                    it.copy(noteEditor = NoteEditor(action.item.id, action.item.notes))
                }

            is CollectionDetailAction.EditNoteText ->
                _state.update { it.copy(noteEditor = it.noteEditor?.copy(notes = action.notes)) }

            CollectionDetailAction.DismissNoteEditor -> _state.update { it.copy(noteEditor = null) }

            CollectionDetailAction.SaveNotes -> viewModelScope.launch { saveNotes() }

            is CollectionDetailAction.RemoveVerse ->
                viewModelScope.launch {
                    repo.removeVerse(action.verseCollectionId)
                    analytics.trackEvent(
                        AnalyticsEvent.VERSE_REMOVED_FROM_COLLECTION,
                        mapOf("collection_id" to route.id),
                    )
                }

            is CollectionDetailAction.RenameCollection ->
                viewModelScope.launch {
                    val name = action.name.trim()
                    if (name.isEmpty()) return@launch

                    repo.updateCollection(route.id, name, action.description)
                    analytics.trackEvent(
                        AnalyticsEvent.COLLECTION_UPDATED,
                        mapOf("collection_id" to route.id),
                    )
                }

            CollectionDetailAction.RequestDelete -> _state.update { it.copy(confirmDelete = true) }

            CollectionDetailAction.DismissDelete -> _state.update { it.copy(confirmDelete = false) }

            CollectionDetailAction.ConfirmDelete ->
                viewModelScope.launch {
                    repo.deleteCollection(route.id)
                    analytics.trackEvent(
                        AnalyticsEvent.COLLECTION_DELETED,
                        mapOf("collection_id" to route.id),
                    )
                    _state.update { it.copy(confirmDelete = false, isDeleted = true) }
                }
        }
    }

    private suspend fun saveNotes() {
        val editor = _state.value.noteEditor ?: return

        repo.updateVerseNotes(editor.verseCollectionId, editor.notes)
        analytics.trackEvent(AnalyticsEvent.VERSE_NOTE_UPDATED, mapOf("collection_id" to route.id))

        _state.update { it.copy(noteEditor = null) }
    }

    private fun observeState() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                repo
                    .getCollectionFlow(route.id)
                    .onEach { collection ->
                        _state.update {
                            it.copy(collection = collection, verses = resolveVerses(collection))
                        }
                    }
                    .launchIn(this)

                datastore
                    .getFontSize()
                    .onEach { size -> _state.update { it.copy(fontSize = size) } }
                    .launchIn(this)

                datastore
                    .getVerseCardState()
                    .onEach { verseCardState ->
                        _state.update { it.copy(verseCardState = verseCardState) }
                    }
                    .launchIn(this)

                // Editing is a Plus feature, so the screen needs the current entitlement.
                billing.isPlus
                    .onEach { isPlus -> _state.update { it.copy(isPlus = isPlus) } }
                    .launchIn(this)
            }
        }

    /** Entries whose chapter or verse cannot be resolved are skipped instead of failing. */
    private suspend fun resolveVerses(collection: Collections?): List<CollectionVerseItem> {
        if (collection == null) return emptyList()

        return collection.verses.mapNotNull { entry ->
            val chapter = chapterVerses(entry.verse.first)
            val verse =
                chapter.firstOrNull { it.verse == entry.verse.second } ?: return@mapNotNull null

            CollectionVerseItem(
                id = entry.id,
                chapter = entry.verse.first,
                verse = entry.verse.second,
                notes = entry.notes,
                gitaVerse = verse,
            )
        }
    }

    private suspend fun chapterVerses(chapter: Long): List<GitaVerse> {
        chapterCache[chapter]?.let {
            return it
        }

        val verses =
            runCatching { bgRepo.getChapter(chapter.toInt()).gitaVerses }.getOrDefault(emptyList())

        chapterCache[chapter] = verses

        return verses
    }
}
