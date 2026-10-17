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
package com.shub39.dharmik.shared.ui.presentation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.navigation.Verses
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.components.VerseCard
import com.shub39.dharmik.shared.ui.util.copyToClipboard
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.arrow_forward_ios
import dharmik.shared.ui.generated.resources.bookmark
import dharmik.shared.ui.generated.resources.favorite
import dharmik.shared.ui.generated.resources.favorites_template
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Home(state: HomeState, onAction: (HomeAction) -> Unit, onNavigateToVerses: (Verses) -> Unit) =
    PageFill {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val clipboardManager = LocalClipboard.current

        LazyColumn(
            state = listState,
            modifier = Modifier.animateContentSize().fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                ListItem(
                    modifier =
                        Modifier.clickable {
                            onNavigateToVerses(
                                Verses(
                                    chapter = state.currentBookMark.first,
                                    verse = state.currentBookMark.second,
                                    saveBookmarks = true,
                                )
                            )
                        },
                    headlineContent = { Text(text = stringResource(Res.string.bookmark)) },
                    supportingContent = {
                        Text(
                            text =
                                "${state.currentBookMark.first} : ${state.currentBookMark.second.plus(1)}"
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_forward_ios),
                            contentDescription = "Open Bookmarks",
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.bookmark),
                            contentDescription = "Bookmarks",
                            modifier = Modifier.size(20.dp),
                        )
                    },
                )
            }

            item { HorizontalDivider() }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text =
                                stringResource(Res.string.favorites_template, state.favorites.size)
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.favorite),
                            contentDescription = "Favorites",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )
            }

            items(state.favorites) { verse ->
                VerseCard(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp).fillMaxWidth(),
                    verse = verse,
                    fontSize = state.fontSize,
                    state = state.verseCardState,
                    isFave = true,
                    onToggleFave = { onAction(HomeAction.SetFave(verse)) },
                    onClick = {
                        onNavigateToVerses(
                            Verses(
                                chapter = verse.chapter,
                                verse = verse.verse.minus(1).coerceAtLeast(0),
                                saveBookmarks = false,
                            )
                        )
                    },
                    onCopy = { coroutineScope.launch { copyToClipboard(clipboardManager, it) } },
                )
            }
        }
    }
