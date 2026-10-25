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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.presentation.collections.components.CollectionEditorDialog
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.add_collection
import dharmik.shared.ui.generated.resources.arrow_forward_ios
import dharmik.shared.ui.generated.resources.collection
import dharmik.shared.ui.generated.resources.collections
import dharmik.shared.ui.generated.resources.collections_empty
import dharmik.shared.ui.generated.resources.new_collection
import dharmik.shared.ui.generated.resources.plus_feature
import dharmik.shared.ui.generated.resources.verses_count
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The list pane of the `Collections` + `CollectionDetail` list-detail scene.
 *
 * Writing is a Plus feature, so every mutating affordance goes through [onRequestWrite], which the
 * navigation layer turns into "do it" or "open the paywall".
 */
@Composable
fun Collections(
    modifier: Modifier = Modifier,
    state: CollectionsState,
    currentSelected: Long?,
    onAction: (CollectionsAction) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onRequestWrite: (() -> Unit) -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeFlexibleTopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.collections),
                            fontFamily = getTitleFont(),
                        )
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = colorScheme.surface
                        ),
                    scrollBehavior = scrollBehavior,
                    actions = {
                        FilledTonalIconButton(
                            onClick = { onRequestWrite { onAction(CollectionsAction.OpenEditor) } }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.add_collection),
                                contentDescription = stringResource(Res.string.new_collection),
                            )
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
                if (state.collections.isEmpty()) {
                    item {
                        CollectionsEmpty(
                            isPlus = state.isPlus,
                            onClick = { onRequestWrite { onAction(CollectionsAction.OpenEditor) } },
                        )
                    }
                } else {
                    itemsIndexed(
                        items = state.collections,
                        key = { _, collection -> collection.id },
                    ) { index, collection ->
                        SegmentedListItem(
                            onClick = { onNavigateToDetail(collection.id) },
                            selected = collection.id == currentSelected,
                            shapes = segmentedListItemShapes(index, state.collections.size),
                            colors =
                                ListItemDefaults.colors(
                                    containerColor = colorScheme.surfaceContainer
                                ),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.collection),
                                    contentDescription = null,
                                )
                            },
                            content = { Text(text = collection.name) },
                            supportingContent = {
                                if (collection.description.isNotBlank()) {
                                    Text(
                                        text = collection.description,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

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
                            trailingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }
            }
        }

        state.editor?.let { editor ->
            CollectionEditorDialog(
                editor = editor,
                onNameChange = { onAction(CollectionsAction.EditName(it)) },
                onDescriptionChange = { onAction(CollectionsAction.EditDescription(it)) },
                onSave = { onAction(CollectionsAction.SaveEditor) },
                onDismiss = { onAction(CollectionsAction.DismissEditor) },
            )
        }
    }

@Composable
private fun CollectionsEmpty(isPlus: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.heightIn(min = 400.dp).fillMaxWidth().padding(horizontal = 16.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClick,
            modifier = Modifier.height(ButtonDefaults.MediumContainerHeight),
        ) {
            Text(
                text =
                    stringResource(
                        if (isPlus) Res.string.new_collection else Res.string.plus_feature
                    ),
                style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
            )
        }
    }
}
