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
package com.shub39.dharmik.shared.ui.presentation.chapters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.arrow_forward_ios
import dharmik.shared.ui.generated.resources.chapter_template
import dharmik.shared.ui.generated.resources.play
import dharmik.shared.ui.generated.resources.play_chapter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The chapter list. A chapter is a playlist of all of its verses, so the leading icon of every row
 * is a play button that starts listening to it.
 */
@Composable
fun Chapters(
    modifier: Modifier = Modifier,
    state: ChaptersState,
    currentSelected: Int?,
    onAction: (ChaptersAction) -> Unit,
    onNavigateToVerses: (chapter: Long) -> Unit,
    onPlayChapter: (chapter: Long) -> Unit,
    onRequestPlay: (() -> Unit) -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        Scaffold(modifier = Modifier.widthIn(max = 700.dp)) { paddingValues ->
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed((1..state.chapters).toList()) { index, chapter ->
                    ListItem(
                        shapes = segmentedListItemShapes(index, state.chapters),
                        selected = index == currentSelected,
                        onClick = {
                            onAction(ChaptersAction.ChapterChange(chapter))
                            onNavigateToVerses(chapter.toLong())
                        },
                        colors =
                            ListItemDefaults.colors(containerColor = colorScheme.surfaceContainer),
                        content = {
                            Text(text = stringResource(Res.string.chapter_template, chapter))
                        },
                        leadingContent = {
                            FilledTonalIconButton(
                                onClick = { onRequestPlay { onPlayChapter(chapter.toLong()) } },
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.play),
                                    contentDescription = stringResource(Res.string.play_chapter),
                                )
                            }
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_forward_ios),
                                contentDescription = "Open Chapter",
                            )
                        },
                    )
                }
            }
        }
    }
