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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.arrow_forward_ios
import dharmik.shared.ui.generated.resources.book
import dharmik.shared.ui.generated.resources.chapter_template
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Chapters(
    modifier: Modifier = Modifier,
    state: ChaptersState,
    onAction: (ChaptersAction) -> Unit,
    onNavigateToVerses: (chapter: Long) -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier.animateContentSize().widthIn(max = 700.dp).fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items((1..state.chapters).toList(), key = { it }) { chapter ->
                ListItem(
                    modifier =
                        Modifier.clickable {
                            onAction(ChaptersAction.ChapterChange(chapter))
                            onNavigateToVerses(chapter.toLong())
                        },
                    headlineContent = {
                        Text(text = stringResource(Res.string.chapter_template, chapter))
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.book),
                            contentDescription = "Chapter",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_forward_ios),
                            contentDescription = "Open Chapter",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )
            }
        }
    }
