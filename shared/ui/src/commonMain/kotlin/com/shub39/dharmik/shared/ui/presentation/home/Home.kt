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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
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
import com.shub39.dharmik.shared.ui.navigation.Verses
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.removeExtraLineBreaks
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import com.shub39.dharmik.shared.ui.theme.getSanskritFont
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.app_icon
import dharmik.shared.ui.generated.resources.app_name
import dharmik.shared.ui.generated.resources.arrow_forward_ios
import dharmik.shared.ui.generated.resources.bookmark
import dharmik.shared.ui.generated.resources.favorite
import dharmik.shared.ui.generated.resources.favorites_empty
import dharmik.shared.ui.generated.resources.settings
import dharmik.shared.ui.generated.resources.start_reading
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun Home(
    modifier: Modifier = Modifier,
    state: HomeState,
    onNavigateToVerses: (Verses) -> Unit,
    onNavigateToSettings: () -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val isAllEmpty =
            state.favorites.isEmpty() &&
                state.currentBookMark.first == 1L &&
                state.currentBookMark.second == 0L

        if (!isAllEmpty) {
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
            ) { paddingValues ->
                LazyColumn(
                    contentPadding =
                        PaddingValues(
                            top = paddingValues.calculateTopPadding() + 16.dp,
                            bottom = paddingValues.calculateBottomPadding() + 60.dp,
                            start =
                                paddingValues.calculateStartPadding(LocalLayoutDirection.current) +
                                    8.dp,
                            end =
                                paddingValues.calculateEndPadding(LocalLayoutDirection.current) +
                                    8.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        ListItem(
                            onClick = {
                                onNavigateToVerses(
                                    Verses(
                                        chapter = state.currentBookMark.first,
                                        verse = state.currentBookMark.second,
                                        saveBookmarks = true,
                                    )
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.bookmark),
                                    contentDescription = null,
                                )
                            },
                            content = { Text(text = stringResource(Res.string.bookmark)) },
                            supportingContent = {
                                val chapter = state.currentBookMark.first
                                val verse = state.currentBookMark.second.plus(1)
                                Text(
                                    text = "Chapter $chapter, Verse $verse",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = null,
                                )
                            },
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    if (state.favorites.isEmpty()) {
                        item {
                            Column(
                                modifier =
                                    Modifier.heightIn(min = 400.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
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
                                        painter = painterResource(Res.drawable.favorite),
                                        contentDescription = "Favorites",
                                        tint = colorScheme.onPrimaryContainer,
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(Res.string.favorites_empty),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    itemsIndexed(state.favorites) { index, verse ->
                        SegmentedListItem(
                            onClick = {
                                onNavigateToVerses(
                                    Verses(
                                        chapter = verse.chapter,
                                        verse = verse.verse.minus(1).coerceAtLeast(0),
                                        saveBookmarks = false,
                                    )
                                )
                            },
                            colors =
                                ListItemDefaults.colors(
                                    containerColor = colorScheme.surfaceContainer
                                ),
                            content = {
                                Text(
                                    text =
                                        when (state.verseCardState) {
                                            ENGLISH -> verse.translations.shriPurohitSwami
                                            HINDI -> verse.translations.swamiTejomayananda
                                            SANSKRIT -> verse.text
                                        }.removeExtraLineBreaks(),
                                    fontFamily =
                                        when (state.verseCardState) {
                                            ENGLISH,
                                            HINDI -> getHindiFont()
                                            SANSKRIT -> getSanskritFont()
                                        },
                                    fontSize = state.fontSize.sp,
                                    lineHeight = state.fontSize.plus(3).sp
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = "Chapter ${verse.chapter}, Verse ${verse.verse}",
                                    color = colorScheme.primary,
                                )
                            },
                            shapes = segmentedListItemShapes(index, state.favorites.size),
                        )
                    }
                }
            }
        } else {
            Surface {
                Column(
                    modifier =
                        Modifier.heightIn(min = 400.dp).fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier =
                            Modifier.size(128.dp)
                                .background(
                                    color = colorScheme.primaryContainer,
                                    shape = MaterialShapes.Sunny.toShape(),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.app_icon),
                            contentDescription = null,
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(72.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.app_name),
                        style =
                            MaterialTheme.typography.headlineLarge.copy(fontFamily = getTitleFont()),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onNavigateToVerses(Verses(chapter = 1, verse = 0, saveBookmarks = true))
                        },
                        modifier = Modifier.height(ButtonDefaults.MediumContainerHeight),
                    ) {
                        Text(
                            text = stringResource(Res.string.start_reading),
                            style =
                                ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
                        )
                    }
                }
            }
        }
    }
