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
package com.shub39.dharmik.shared.ui.presentation.verses

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.core.media.MediaPlayerState
import com.shub39.dharmik.shared.ui.navigation.LocalBackButtonVisibility
import com.shub39.dharmik.shared.ui.presentation.components.ListSelect
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.components.VerseCard
import com.shub39.dharmik.shared.ui.presentation.verses.components.CommentariesDisplay
import com.shub39.dharmik.shared.ui.presentation.verses.components.TranslationsDisplay
import com.shub39.dharmik.shared.ui.util.copyToClipboard
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.add_collection
import dharmik.shared.ui.generated.resources.add_to_collection
import dharmik.shared.ui.generated.resources.arrow_back
import dharmik.shared.ui.generated.resources.arrow_forward
import dharmik.shared.ui.generated.resources.autoplay
import dharmik.shared.ui.generated.resources.bhagvad_gita
import dharmik.shared.ui.generated.resources.chapter_template
import dharmik.shared.ui.generated.resources.check_list
import dharmik.shared.ui.generated.resources.content_copy
import dharmik.shared.ui.generated.resources.favorite
import dharmik.shared.ui.generated.resources.favorite_border
import dharmik.shared.ui.generated.resources.pause
import dharmik.shared.ui.generated.resources.play
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Verses(
    modifier: Modifier = Modifier,
    state: VersesState,
    action: (VersesAction) -> Unit,
    onBack: () -> Unit,
    onAddToCollection: (GitaVerse) -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val verses = state.currentVerses

        // The view model loads the chapter asynchronously, so there is a frame where it is still
        // empty.
        if (verses.isEmpty()) return@PageFill

        val clipboardManager = LocalClipboard.current
        val coroutineScope = rememberCoroutineScope()

        var sliderPosition by remember { mutableFloatStateOf(0f) }
        var menuExpanded by remember { mutableStateOf(false) }

        val playerState by state.player.state.collectAsState()

        LaunchedEffect(state.pagerState.currentPage) {
            sliderPosition = state.pagerState.currentPage.toFloat()
            action(VersesAction.ChangeVerse(state.pagerState.currentPage, coroutineScope))
        }

        LaunchedEffect(playerState) {
            if (playerState == MediaPlayerState.Completed) {
                if (state.autoPlay && state.pagerState.currentPage < verses.size - 1) {
                    action(
                        VersesAction.ChangeVerse(state.pagerState.currentPage + 1, coroutineScope)
                    )
                } else {
                    action(VersesAction.Pause)
                }
            }
        }

        DisposableEffect(Unit) { onDispose { state.player.release() } }

        Scaffold(
            modifier = Modifier.widthIn(max = 700.dp),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text =
                                if (!state.saveBookMarks) {
                                    stringResource(Res.string.bhagvad_gita)
                                } else {
                                    stringResource(
                                        Res.string.chapter_template,
                                        verses.first().chapter,
                                    )
                                }
                        )
                    },
                    navigationIcon = {
                        // Hidden in the detail pane of a list-detail scene, which owns its own back
                        // affordance.
                        if (LocalBackButtonVisibility.current) {
                            FilledTonalIconButton(
                                onClick = {
                                    action(VersesAction.Pause)
                                    onBack()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_back),
                                    contentDescription = "Go Back",
                                )
                            }
                        }
                    },
                    actions = {
                        Box {
                            FilledTonalIconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    painter = painterResource(Res.drawable.check_list),
                                    contentDescription = "More options",
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(Res.string.add_to_collection))
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.add_collection),
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        verses.getOrNull(state.pagerState.currentPage)?.let { verse
                                            ->
                                            onAddToCollection(verse)
                                        }
                                    },
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    Row {
                        FilledTonalIconButton(
                            onClick = {
                                action(
                                    VersesAction.ChangeVerse(
                                        state.pagerState.currentPage - 1,
                                        coroutineScope,
                                    )
                                )
                            },
                            enabled = state.pagerState.currentPage > 0,
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back),
                                contentDescription = "Previous",
                            )
                        }

                        Slider(
                            modifier = Modifier.weight(1f),
                            value = sliderPosition,
                            steps =
                                when {
                                    verses.size > 100 -> (verses.size - 2) / 5
                                    verses.size > 50 -> (verses.size - 2) / 3
                                    else -> verses.size - 2
                                }.coerceAtLeast(0),
                            valueRange = 0f..verses.size.toFloat().minus(1),
                            onValueChange = {
                                action(VersesAction.ChangeVerse(it.toInt(), coroutineScope))
                            },
                        )

                        FilledTonalIconButton(
                            onClick = {
                                action(
                                    VersesAction.ChangeVerse(
                                        state.pagerState.currentPage + 1,
                                        coroutineScope,
                                    )
                                )
                            },
                            shapes = IconButtonDefaults.shapes(),
                            enabled = state.pagerState.currentPage < verses.size - 1,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_forward),
                                contentDescription = "Next",
                            )
                        }
                    }
                }
            },
        ) { padding ->
            HorizontalPager(
                state = state.pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = padding.calculateLeftPadding(LocalLayoutDirection.current),
                        end = padding.calculateRightPadding(LocalLayoutDirection.current),
                    ),
            ) { index ->
                val currentVerse = verses[index]
                val scrollState = rememberLazyListState()

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = padding.calculateTopPadding() + 16.dp,
                            bottom = padding.calculateBottomPadding() + 60.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            ToggleButton(
                                checked = state.favorites.contains(currentVerse),
                                onCheckedChange = { action(VersesAction.SetFave(currentVerse)) },
                                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    painter =
                                        painterResource(
                                            if (state.favorites.contains(currentVerse)) {
                                                Res.drawable.favorite
                                            } else {
                                                Res.drawable.favorite_border
                                            }
                                        ),
                                    contentDescription = null,
                                )
                            }

                            ToggleButton(
                                checked = false,
                                onCheckedChange = {
                                    coroutineScope.launch {
                                        copyToClipboard(
                                            clipboardManager,
                                            when (state.verseCardState) {
                                                ENGLISH ->
                                                    currentVerse.translations.shriPurohitSwami
                                                HINDI ->
                                                    currentVerse.translations.swamiTejomayananda
                                                SANSKRIT -> currentVerse.text
                                            },
                                        )
                                    }
                                },
                                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.content_copy),
                                    contentDescription = null,
                                )
                            }

                            ToggleButton(
                                checked = state.isPlaying,
                                onCheckedChange = {
                                    if (state.isPlaying) {
                                        action(VersesAction.Pause)
                                    } else {
                                        action(VersesAction.Play(index))
                                    }
                                },
                                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    painter =
                                        painterResource(
                                            if (state.isPlaying) {
                                                Res.drawable.pause
                                            } else {
                                                Res.drawable.play
                                            }
                                        ),
                                    contentDescription = "Play/Pause",
                                )
                            }

                            ToggleButton(
                                checked = state.autoPlay,
                                onCheckedChange = {
                                    action(VersesAction.SetAutoPlay(!state.autoPlay))
                                },
                                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.autoplay),
                                    contentDescription = "Autoplay",
                                )
                            }
                        }
                    }

                    item {
                        AnimatedContent(targetState = state.verseCardState) { vcState ->
                            VerseCard(
                                verse = currentVerse,
                                fontSize = state.fontSize,
                                modifier = Modifier.fillMaxWidth(),
                                state = vcState,
                            )
                        }
                    }

                    item {
                        ListSelect(
                            title = null,
                            options = VerseCardState.entries.toList(),
                            selected = state.verseCardState,
                            onSelectedChange = { action(VersesAction.SetVerseCardState(it)) },
                            labelProvider = { Text(text = it.fullName) },
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        TranslationsDisplay(
                            translations = currentVerse.translations,
                            onCopy = {
                                coroutineScope.launch { copyToClipboard(clipboardManager, it) }
                            },
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        CommentariesDisplay(
                            commentaries = currentVerse.commentaries,
                            onCopy = {
                                coroutineScope.launch { copyToClipboard(clipboardManager, it) }
                            },
                        )
                    }
                }
            }
        }
    }
