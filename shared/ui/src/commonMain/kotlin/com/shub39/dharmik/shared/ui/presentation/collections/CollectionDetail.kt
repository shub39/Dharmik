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
package com.shub39.dharmik.shared.ui.presentation.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.dharmik.shared.core.domain.VerseCardState.ENGLISH
import com.shub39.dharmik.shared.core.domain.VerseCardState.HINDI
import com.shub39.dharmik.shared.core.domain.VerseCardState.SANSKRIT
import com.shub39.dharmik.shared.ui.navigation.LocalBackButtonVisibility
import com.shub39.dharmik.shared.ui.presentation.collections.components.CollectionEditorDialog
import com.shub39.dharmik.shared.ui.presentation.collections.components.NoteEditorDialog
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.removeExtraLineBreaks
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import com.shub39.dharmik.shared.ui.theme.getSanskritFont
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.arrow_back
import dharmik.shared.ui.generated.resources.cancel
import dharmik.shared.ui.generated.resources.collection
import dharmik.shared.ui.generated.resources.collections_empty
import dharmik.shared.ui.generated.resources.delete
import dharmik.shared.ui.generated.resources.delete_collection
import dharmik.shared.ui.generated.resources.delete_collection_message
import dharmik.shared.ui.generated.resources.edit
import dharmik.shared.ui.generated.resources.edit_collection
import dharmik.shared.ui.generated.resources.edit_note
import dharmik.shared.ui.generated.resources.more
import dharmik.shared.ui.generated.resources.more_options
import dharmik.shared.ui.generated.resources.navigate_back
import dharmik.shared.ui.generated.resources.remove_verse
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The detail pane of the `Collections` + `CollectionDetail` list-detail scene.
 *
 * The collected verses are stored as chapter/verse numbers only, so their text follows whatever
 * language the reader is set to.
 */
@Composable
fun CollectionDetail(
    modifier: Modifier = Modifier,
    state: CollectionDetailState,
    onAction: (CollectionDetailAction) -> Unit,
    onNavigateToVerses: (chapter: Long, verse: Long) -> Unit,
    onBack: () -> Unit,
    onRequestWrite: (() -> Unit) -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val collection = state.collection
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        var menuExpanded by remember { mutableStateOf(false) }
        var renaming by remember { mutableStateOf(false) }
        var renameName by remember { mutableStateOf("") }
        var renameDescription by remember { mutableStateOf("") }

        LaunchedEffect(state.isDeleted) { if (state.isDeleted) onBack() }

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = collection?.name.orEmpty(), fontFamily = getTitleFont())
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = colorScheme.surface
                        ),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        // Hidden in the detail pane of a list-detail scene, which owns its own
                        // back affordance.
                        if (LocalBackButtonVisibility.current) {
                            FilledTonalIconButton(onClick = onBack) {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_back),
                                    contentDescription = stringResource(Res.string.navigate_back),
                                )
                            }
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    painter = painterResource(Res.drawable.more),
                                    contentDescription = stringResource(Res.string.more_options),
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.edit_collection)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.edit),
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onRequestWrite {
                                            renameName = collection?.name.orEmpty()
                                            renameDescription = collection?.description.orEmpty()
                                            renaming = true
                                        }
                                    },
                                )

                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.delete_collection)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.delete),
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onRequestWrite {
                                            onAction(CollectionDetailAction.RequestDelete)
                                        }
                                    },
                                )
                            }
                        }
                    },
                )
            },
        ) { paddingValues ->
            LazyColumn(
                contentPadding =
                    PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                        start =
                            paddingValues.calculateStartPadding(LocalLayoutDirection.current) +
                                8.dp,
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 8.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (!collection?.description.isNullOrBlank()) {
                    item {
                        Text(
                            text = collection.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        )
                    }
                }

                if (state.verses.isEmpty()) {
                    item { VersesEmpty() }
                } else {
                    itemsIndexed(items = state.verses, key = { _, item -> item.id }) { index, item
                        ->
                        SegmentedListItem(
                            onClick = { onNavigateToVerses(item.chapter, item.verse) },
                            shapes = segmentedListItemShapes(index, state.verses.size),
                            colors =
                                ListItemDefaults.colors(
                                    containerColor = colorScheme.surfaceContainer
                                ),
                            content = {
                                Text(
                                    text =
                                        when (state.verseCardState) {
                                            ENGLISH -> item.gitaVerse.translations.shriPurohitSwami
                                            HINDI -> item.gitaVerse.translations.swamiTejomayananda
                                            SANSKRIT -> item.gitaVerse.text
                                        }.removeExtraLineBreaks(),
                                    fontFamily =
                                        when (state.verseCardState) {
                                            ENGLISH,
                                            HINDI -> getHindiFont()
                                            SANSKRIT -> getSanskritFont()
                                        },
                                    fontSize = state.fontSize.sp,
                                    lineHeight = state.fontSize.plus(3).sp,
                                )
                            },
                            supportingContent = {
                                Column {
                                    Text(
                                        text = "Chapter ${item.chapter}, Verse ${item.verse}",
                                        color = colorScheme.primary,
                                    )

                                    if (item.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = item.notes,
                                            color = colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                Row {
                                    IconButton(
                                        onClick = {
                                            onRequestWrite {
                                                onAction(CollectionDetailAction.EditNotes(item))
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.edit),
                                            contentDescription =
                                                stringResource(Res.string.edit_note),
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            onRequestWrite {
                                                onAction(
                                                    CollectionDetailAction.RemoveVerse(item.id)
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.delete),
                                            contentDescription =
                                                stringResource(Res.string.remove_verse),
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }

        state.noteEditor?.let { noteEditor ->
            NoteEditorDialog(
                notes = noteEditor.notes,
                onNotesChange = { onAction(CollectionDetailAction.EditNoteText(it)) },
                onSave = { onAction(CollectionDetailAction.SaveNotes) },
                onDismiss = { onAction(CollectionDetailAction.DismissNoteEditor) },
            )
        }

        if (renaming) {
            CollectionEditorDialog(
                editor = CollectionEditor(collection?.id, renameName, renameDescription),
                onNameChange = { renameName = it },
                onDescriptionChange = { renameDescription = it },
                onSave = {
                    onAction(CollectionDetailAction.RenameCollection(renameName, renameDescription))
                    renaming = false
                },
                onDismiss = { renaming = false },
            )
        }

        if (state.confirmDelete) {
            AlertDialog(
                onDismissRequest = { onAction(CollectionDetailAction.DismissDelete) },
                icon = {
                    Icon(painter = painterResource(Res.drawable.delete), contentDescription = null)
                },
                title = { Text(text = stringResource(Res.string.delete_collection)) },
                text = { Text(text = stringResource(Res.string.delete_collection_message)) },
                confirmButton = {
                    TextButton(onClick = { onAction(CollectionDetailAction.ConfirmDelete) }) {
                        Text(text = stringResource(Res.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(CollectionDetailAction.DismissDelete) }) {
                        Text(text = stringResource(Res.string.cancel))
                    }
                },
            )
        }
    }

@Composable
private fun VersesEmpty() {
    Column(
        modifier = Modifier.heightIn(min = 320.dp).fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier.size(48.dp)
                    .background(
                        color = colorScheme.primaryContainer,
                        shape = MaterialShapes.Sunny.toShape(),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.collection),
                contentDescription = null,
                tint = colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(Res.string.collections_empty), textAlign = TextAlign.Center)
    }
}
