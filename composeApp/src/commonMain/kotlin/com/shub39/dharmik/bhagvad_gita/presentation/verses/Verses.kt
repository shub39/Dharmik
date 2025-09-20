package com.shub39.dharmik.bhagvad_gita.presentation.verses

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.ui.audio.AudioPlayer
import com.shub39.dharmik.bhagvad_gita.presentation.components.VerseCard
import com.shub39.dharmik.bhagvad_gita.presentation.verses.components.CommentariesDisplay
import com.shub39.dharmik.bhagvad_gita.presentation.verses.components.TranslationsDisplay
import com.shub39.dharmik.core.domain.VerseCardState
import com.shub39.dharmik.core.presentation.components.PageFill
import com.shub39.dharmik.core.presentation.copyToClipboard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.FastForward
import compose.icons.fontawesomeicons.solid.Pause
import compose.icons.fontawesomeicons.solid.Play
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.bhagvad_gita
import dharmik.composeapp.generated.resources.chapter_template
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Verses(
    state: VersesState,
    action: (VersesAction) -> Unit,
    onBack: () -> Unit
) = PageFill {
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    var sliderPosition by remember { mutableFloatStateOf(0f) }

    val verses = state.currentVerses

    LaunchedEffect(state.pagerState.currentPage) {
        sliderPosition = state.pagerState.currentPage.toFloat()
        action(VersesAction.ChangeVerse(state.pagerState.currentPage, coroutineScope))
    }

    AudioPlayer(state.playerHost)

    state.playerHost.onEvent = { event ->
        if (event == MediaPlayerEvent.MediaEnd && state.autoPlay) {
            if (state.pagerState.currentPage < verses.size - 1) {
                action(VersesAction.ChangeVerse((state.pagerState.currentPage + 1), coroutineScope))
            } else {
                action(VersesAction.Pause)
            }
        } else if (event == MediaPlayerEvent.MediaEnd) {
            action(VersesAction.Pause)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            state.playerHost.pause()
        }
    }

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (!state.saveBookMarks) {
                            stringResource(Res.string.bhagvad_gita)
                        } else {
                            stringResource(
                                Res.string.chapter_template,
                                verses.first().chapter
                            )
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = { action(VersesAction.SetAutoPlay(!state.autoPlay)) },
                        colors = if (state.autoPlay) {
                            IconButtonDefaults.filledTonalIconButtonColors()
                        } else {
                            IconButtonDefaults.iconButtonColors()
                        }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.FastForward,
                            contentDescription = "Autoplay",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            state.playerHost.pause()
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                Row {
                    IconButton(
                        onClick = {
                            action(VersesAction.ChangeVerse(state.pagerState.currentPage - 1, coroutineScope))
                        },
                        enabled = state.pagerState.currentPage > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Previous"
                        )
                    }

                    Slider(
                        modifier = Modifier.weight(1f),
                        value = sliderPosition,
                        steps = when {
                            verses.size > 100 -> (verses.size - 2) / 5
                            verses.size > 50 -> (verses.size - 2) / 3
                            else -> verses.size - 2
                        }.coerceAtLeast(0),
                        valueRange = 0f..verses.size.toFloat().minus(1),
                        onValueChange = {
                            action(VersesAction.ChangeVerse(it.toInt(), coroutineScope))
                        }
                    )

                    IconButton(
                        onClick = {
                            action(VersesAction.ChangeVerse(state.pagerState.currentPage + 1, coroutineScope))
                        },
                        enabled = state.pagerState.currentPage < verses.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = state.pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding
        ) { index ->
            val currentVerse = verses[index]
            val scrollState = rememberLazyListState()

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        VerseCardState.entries.forEach { vcState ->
                            SegmentedButton(
                                selected = vcState == state.verseCardState,
                                onClick = {
                                    action(VersesAction.SetVerseCardState(vcState))
                                },
                                shape = when (vcState) {
                                    VerseCardState.ENGLISH -> RoundedCornerShape(
                                        topStart = 20.dp,
                                        bottomStart = 20.dp
                                    )

                                    VerseCardState.HINDI -> RectangleShape

                                    VerseCardState.SANSKRIT -> RoundedCornerShape(
                                        topEnd = 20.dp,
                                        bottomEnd = 20.dp
                                    )
                                },
                                label = { Text(vcState.fullName) }
                            )
                        }
                    }
                }

                item {
                    AnimatedContent(
                        targetState = state.verseCardState
                    ) { vcState ->
                        VerseCard(
                            verse = currentVerse,
                            fontSize = state.fontSize,
                            modifier = Modifier.fillMaxWidth(),
                            isFave = state.favorites.contains(currentVerse),
                            state = vcState,
                            action = action,
                            onClick = {},
                            onCopy = {
                                coroutineScope.launch {
                                    copyToClipboard(clipboardManager, it)
                                }
                            },
                            playIcon = {
                                IconButton(
                                    onClick = {
                                        if (state.isPlaying) {
                                            action(VersesAction.Pause)
                                        } else {
                                            action(VersesAction.Play(index))
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (state.isPlaying) {
                                            FontAwesomeIcons.Solid.Pause
                                        } else {
                                            FontAwesomeIcons.Solid.Play
                                        },
                                        contentDescription = "Play/Pause",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )
                    }
                }

                item {
                    TranslationsDisplay(
                        translations = currentVerse.translations,
                        onCopy = {
                            coroutineScope.launch {
                                copyToClipboard(clipboardManager, it)
                            }
                        },
                        fontSize = state.fontSize
                    )
                }

                item {
                    CommentariesDisplay(
                        commentaries = currentVerse.commentaries,
                        onCopy = {
                            coroutineScope.launch {
                                copyToClipboard(clipboardManager, it)
                            }
                        },
                        fontSize = state.fontSize
                    )
                }

                item {
                    Spacer(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}