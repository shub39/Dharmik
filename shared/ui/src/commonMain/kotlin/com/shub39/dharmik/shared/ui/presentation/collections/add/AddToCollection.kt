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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.presentation.collections.components.CollectionEditorDialog
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.components.VerseCard
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.add_collection
import dharmik.shared.ui.generated.resources.add_to_collection
import dharmik.shared.ui.generated.resources.arrow_back
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
 * Collections are picked from the list, and a brand new one can be created inline through the same
 * dialog the collections screen uses.
 */
@Composable
fun AddToCollection(
    modifier: Modifier = Modifier,
    state: AddToCollectionState,
    onAction: (AddToCollectionAction) -> Unit,
    onBack: () -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
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
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().animateContentSize(),
                contentPadding =
                    PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                        start =
                            paddingValues.calculateStartPadding(LocalLayoutDirection.current) +
                                16.dp,
                        end =
                            paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
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
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = { onAction(AddToCollectionAction.SetNote(it)) },
                        label = { Text(text = stringResource(Res.string.notes)) },
                        minLines = 2,
                        keyboardOptions =
                            KeyboardOptions(
                                imeAction = ImeAction.Done,
                                capitalization = KeyboardCapitalization.Sentences,
                            ),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
                    )
                }

                item {
                    Text(
                        text = stringResource(Res.string.collections),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier,
                    )
                }

                itemsIndexed(items = state.collections, key = { _, collection -> collection.id }) {
                    index,
                    collection ->
                    val checked = collection.id in state.selectedIds

                    SegmentedListItem(
                        selected = checked,
                        onClick = {
                            onAction(AddToCollectionAction.ToggleCollection(collection.id))
                        },
                        shapes = segmentedListItemShapes(index, state.collections.size),
                        colors =
                            ListItemDefaults.colors(containerColor = colorScheme.surfaceContainer),
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
                    OutlinedButton(
                        onClick = { onAction(AddToCollectionAction.OpenEditor) },
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(top = 8.dp)
                                .height(ButtonDefaults.MediumContainerHeight),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.add_collection),
                            contentDescription = null,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(Res.string.new_collection),
                            style =
                                ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Button(
                            onClick = { onAction(AddToCollectionAction.Save) },
                            enabled =
                                state.verse != null &&
                                    !state.isSaving &&
                                    state.selectedIds.isNotEmpty(),
                            modifier =
                                Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
                        ) {
                            Text(
                                text = stringResource(Res.string.save),
                                style =
                                    ButtonDefaults.textStyleFor(
                                        ButtonDefaults.MediumContainerHeight
                                    ),
                            )
                        }
                    }
                }
            }
        }

        state.editor?.let { editor ->
            CollectionEditorDialog(
                editor = editor,
                onNameChange = { onAction(AddToCollectionAction.EditName(it)) },
                onDescriptionChange = { onAction(AddToCollectionAction.EditDescription(it)) },
                onSave = { onAction(AddToCollectionAction.SaveEditor) },
                onDismiss = { onAction(AddToCollectionAction.DismissEditor) },
            )
        }
    }
