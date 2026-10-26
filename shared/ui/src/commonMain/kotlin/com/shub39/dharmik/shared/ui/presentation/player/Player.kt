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
package com.shub39.dharmik.shared.ui.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.dharmik.shared.core.domain.AudioVariant
import com.shub39.dharmik.shared.core.domain.PlaybackState
import com.shub39.dharmik.shared.core.domain.PlaylistSource
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.core.media.MediaPlayerState
import com.shub39.dharmik.shared.ui.presentation.components.ListSelect
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.player.components.PlaylistBottomSheet
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import com.shub39.dharmik.shared.ui.theme.getSanskritFont
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.chapter_template
import dharmik.shared.ui.generated.resources.headphones
import dharmik.shared.ui.generated.resources.next
import dharmik.shared.ui.generated.resources.nothing_playing
import dharmik.shared.ui.generated.resources.nothing_playing_desc
import dharmik.shared.ui.generated.resources.pause
import dharmik.shared.ui.generated.resources.play
import dharmik.shared.ui.generated.resources.play_pause
import dharmik.shared.ui.generated.resources.player
import dharmik.shared.ui.generated.resources.player_feature
import dharmik.shared.ui.generated.resources.plus_feature
import dharmik.shared.ui.generated.resources.previous
import dharmik.shared.ui.generated.resources.queue
import dharmik.shared.ui.generated.resources.queue_music
import dharmik.shared.ui.generated.resources.retry
import dharmik.shared.ui.generated.resources.skip_next
import dharmik.shared.ui.generated.resources.skip_previous
import dharmik.shared.ui.generated.resources.variant_english
import dharmik.shared.ui.generated.resources.variant_hindi
import dharmik.shared.ui.generated.resources.variant_mool
import dharmik.shared.ui.generated.resources.verses_alt_template
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The audio player: what is playing, the standard transport controls and the queue of the session.
 *
 * The session itself is app wide, so this screen only starts a new queue when it was opened from a
 * chapter or a collection. Listening to playlists is a Plus feature, so an entitled-only empty
 * state takes the place of the controls otherwise.
 */
@Composable
fun Player(
    modifier: Modifier = Modifier,
    state: PlayerState,
    onAction: (PlayerAction) -> Unit,
    onNavigateToPaywall: () -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val playback = state.playback
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeFlexibleTopAppBar(
                    title = { Text(text = playerTitle(playback), fontFamily = getTitleFont()) },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = colorScheme.surface
                        ),
                    scrollBehavior = scrollBehavior,
                    actions = {
                        FilledTonalIconButton(
                            onClick = { onAction(PlayerAction.OpenQueue) },
                            enabled = state.isPlus && playback.playlist != null,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.queue_music),
                                contentDescription = stringResource(Res.string.queue),
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            if (!state.isPlus) {
                PlayerLocked(
                    modifier =
                        Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
                    onClick = onNavigateToPaywall,
                )
            } else if (playback.playlist == null) {
                NothingPlaying(
                    modifier =
                        Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)
                )
            } else {
                Column(
                    modifier =
                        Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    NowPlayingCard(state = state)

                    ListSelect(
                        title = null,
                        options = AudioVariant.entries.toList(),
                        selected = playback.variant,
                        onSelectedChange = { onAction(PlayerAction.SetVariant(it)) },
                        labelProvider = { Text(text = it.label()) },
                    )

                    SeekBar(playback = playback, onSeek = { onAction(PlayerAction.SeekTo(it)) })

                    Controls(
                        isPlaying = playback.isPlaying,
                        onPrevious = { onAction(PlayerAction.Previous) },
                        onPlayPause = { onAction(PlayerAction.PlayPause) },
                        onNext = { onAction(PlayerAction.Next) },
                    )

                    if (playback.playerState is MediaPlayerState.Error) {
                        FilledTonalButton(onClick = { onAction(PlayerAction.Retry) }) {
                            Text(text = stringResource(Res.string.retry))
                        }
                    }

                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        if (state.isPlus && state.queueSheetOpen) {
            PlaylistBottomSheet(
                playback = playback,
                onSelect = { onAction(PlayerAction.SelectTrack(it)) },
                onDismissRequest = { onAction(PlayerAction.DismissQueue) },
            )
        }
    }

@Composable
private fun playerTitle(playback: PlaybackState): String =
    when (val source = playback.playlist?.source) {
        is PlaylistSource.Chapter -> stringResource(Res.string.chapter_template, source.chapter)
        is PlaylistSource.Collection -> source.name
        null -> stringResource(Res.string.player)
    }

@Composable
private fun NowPlayingCard(state: PlayerState) {
    val track = state.playback.currentTrack

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes.extraLarge,
        color = colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                    track?.let { stringResource(Res.string.verses_alt_template, it.verse) }
                        ?: stringResource(Res.string.player),
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = getTitleFont(),
                color = colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.verseText,
                fontFamily = state.verseCardState.fontFamily(),
                fontSize = 20.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SeekBar(playback: PlaybackState, onSeek: (Long) -> Unit) {
    val duration = playback.durationMillis.coerceAtLeast(0L)
    val progress =
        if (duration > 0L) (playback.positionMillis.toFloat() / duration).coerceIn(0f, 1f) else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = progress,
            onValueChange = { onSeek((it * duration).toLong()) },
            enabled = duration > 0L,
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatDuration(playback.positionMillis),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Controls(
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledTonalIconButton(onClick = onPrevious, shapes = IconButtonDefaults.shapes()) {
            Icon(
                painter = painterResource(Res.drawable.skip_previous),
                contentDescription = stringResource(Res.string.previous),
            )
        }

        FilledIconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(72.dp),
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                painter = painterResource(if (isPlaying) Res.drawable.pause else Res.drawable.play),
                contentDescription = stringResource(Res.string.play_pause),
                modifier = Modifier.size(32.dp),
            )
        }

        FilledTonalIconButton(onClick = onNext, shapes = IconButtonDefaults.shapes()) {
            Icon(
                painter = painterResource(Res.drawable.skip_next),
                contentDescription = stringResource(Res.string.next),
            )
        }
    }
}

/** Shown instead of the transport controls when the user is not entitled to playlists. */
@Composable
private fun PlayerLocked(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.heightIn(min = 400.dp),
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
                painter = painterResource(Res.drawable.headphones),
                contentDescription = null,
                tint = colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(Res.string.player_feature), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClick,
            modifier = Modifier.height(ButtonDefaults.MediumContainerHeight),
        ) {
            Text(
                text = stringResource(Res.string.plus_feature),
                style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
            )
        }
    }
}

@Composable
private fun NothingPlaying(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.heightIn(min = 400.dp),
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
                painter = painterResource(Res.drawable.queue_music),
                contentDescription = null,
                tint = colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.nothing_playing),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.nothing_playing_desc),
            textAlign = TextAlign.Center,
            color = colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AudioVariant.label(): String =
    when (this) {
        AudioVariant.MOOL -> stringResource(Res.string.variant_mool)
        AudioVariant.HINDI -> stringResource(Res.string.variant_hindi)
        AudioVariant.ENGLISH -> stringResource(Res.string.variant_english)
    }

@Composable
private fun VerseCardState.fontFamily(): FontFamily =
    when (this) {
        VerseCardState.SANSKRIT -> getSanskritFont()
        VerseCardState.HINDI,
        VerseCardState.ENGLISH -> getHindiFont()
    }

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis.coerceAtLeast(0L) / 1000L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}
