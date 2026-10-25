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
package com.shub39.dharmik.shared.ui.presentation.collections.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.components.VerseCard
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.add_to_collection
import dharmik.shared.ui.generated.resources.arrow_back
import dharmik.shared.ui.generated.resources.collection_default_name
import dharmik.shared.ui.generated.resources.collection_name
import dharmik.shared.ui.generated.resources.collections
import dharmik.shared.ui.generated.resources.navigate_back
import dharmik.shared.ui.generated.resources.new_collection
import dharmik.shared.ui.generated.resources.notes
import dharmik.shared.ui.generated.resources.save
import dharmik.shared.ui.generated.resources.verses_count
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Collects the verse carried by the route.
 *
 * When the user has no collections yet, the "new collection" field is pre-filled so the first save
 * always has somewhere to put the verse.
 */
@Composable
fun AddToCollection(
    modifier: Modifier = Modifier,
    state: AddToCollectionState,
    onAction: (AddToCollectionAction) -> Unit,
    onBack: () -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val defaultName = stringResource(Res.string.collection_default_name)

        LaunchedEffect(state.collections.isEmpty(), state.newCollectionName) {
            if (state.collections.isEmpty() && state.newCollectionName.isBlank()) {
                onAction(AddToCollectionAction.SetNewCollectionName(defaultName))
            }
        }

        LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }

        Scaffold(
            modifier = Modifier.widthIn(max = 700.dp).fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.add_to_collection),
                            fontFamily = getTitleFont(),
                        )
                    },
                    navigationIcon = {
                        FilledTonalIconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back),
                                contentDescription = stringResource(Res.string.navigate_back),
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { onAction(AddToCollectionAction.Save) },
                            enabled = state.verse != null && !state.isSaving,
                        ) {
                            Text(text = stringResource(Res.string.save))
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.verse?.let { verse ->
                    item {
                        VerseCard(
                            verse = verse,
                            fontSize = state.fontSize,
                            state = state.verseCardState,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(Res.string.collections),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    )
                }

                itemsIndexed(items = state.collections, key = { _, collection -> collection.id }) {
                    index,
                    collection ->
                    val checked = collection.id in state.selectedIds

                    SegmentedListItem(
                        onClick = {
                            onAction(AddToCollectionAction.ToggleCollection(collection.id))
                        },
                        shapes = segmentedListItemShapes(index, state.collections.size),
                        colors =
                            ListItemDefaults.colors(containerColor = colorScheme.surfaceContainer),
                        leadingContent = {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    onAction(AddToCollectionAction.ToggleCollection(collection.id))
                                },
                            )
                        },
                        content = { Text(text = collection.name) },
                        supportingContent = {
                            Text(
                                text =
                                    pluralStringResource(
                                        Res.plurals.verses_count,
                                        collection.verses.size,
                                        collection.verses.size,
                                    ),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                    )
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.new_collection),
                            style = MaterialTheme.typography.titleMedium,
                        )

                        OutlinedTextField(
                            value = state.newCollectionName,
                            onValueChange = {
                                onAction(AddToCollectionAction.SetNewCollectionName(it))
                            },
                            label = { Text(text = stringResource(Res.string.collection_name)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = { onAction(AddToCollectionAction.SetNote(it)) },
                        label = { Text(text = stringResource(Res.string.notes)) },
                        minLines = 2,
                        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                    )
                }
            }
        }
    }
