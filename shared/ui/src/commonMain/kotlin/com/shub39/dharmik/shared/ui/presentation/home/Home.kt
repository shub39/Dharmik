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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.navigation.Verses
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.components.VerseCard
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import com.shub39.dharmik.shared.ui.util.copyToClipboard
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.app_name
import dharmik.shared.ui.generated.resources.arrow_forward_ios
import dharmik.shared.ui.generated.resources.bookmark
import dharmik.shared.ui.generated.resources.favorite
import dharmik.shared.ui.generated.resources.favorites_template
import dharmik.shared.ui.generated.resources.settings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun Home(
    modifier: Modifier = Modifier,
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToVerses: (Verses) -> Unit,
    onNavigateToSettings: () -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val coroutineScope = rememberCoroutineScope()
        val clipboardManager = LocalClipboard.current

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
                            text = stringResource(Res.string.app_name),
                            fontFamily = getTitleFont(),
                        )
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = colorScheme.surface
                        ),
                    scrollBehavior = scrollBehavior,
                    actions = {
                        FilledTonalIconButton(onClick = onNavigateToSettings) {
                            Icon(
                                painter = painterResource(Res.drawable.settings),
                                contentDescription = "Settings",
                            )
                        }
                    },
                )
            },
        ) { padding ->
            LazyColumn(
                contentPadding =
                    PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 60.dp,
                        start = padding.calculateStartPadding(LocalLayoutDirection.current) + 8.dp,
                        end = padding.calculateEndPadding(LocalLayoutDirection.current) + 8.dp,
                    )
            ) {
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        onClick = {
                            onNavigateToVerses(
                                Verses(
                                    chapter = state.currentBookMark.first,
                                    verse = state.currentBookMark.second,
                                    saveBookmarks = true,
                                )
                            )
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.bookmark),
                                contentDescription = null,
                                tint = colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Continue Reading",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                val chapter = state.currentBookMark.first
                                val verse = state.currentBookMark.second.plus(1)
                                Text(
                                    text = "Chapter $chapter, Verse $verse",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }

                            IconButton(
                                onClick = {
                                    onNavigateToVerses(
                                        Verses(
                                            chapter = state.currentBookMark.first,
                                            verse = state.currentBookMark.second,
                                            saveBookmarks = true,
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.favorite),
                            contentDescription = "Favorites",
                        )
                        Text(
                            text =
                                stringResource(Res.string.favorites_template, state.favorites.size),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                items(state.favorites) { verse ->
                    VerseCard(
                        modifier =
                            Modifier.padding(vertical = 4.dp, horizontal = 16.dp).fillMaxWidth(),
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
    }
