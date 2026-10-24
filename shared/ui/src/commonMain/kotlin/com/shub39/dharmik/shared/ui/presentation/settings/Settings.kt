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
package com.shub39.dharmik.shared.ui.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.core.domain.AppTheme
import com.shub39.dharmik.shared.core.domain.BulkAudioState
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.ui.detachedItemShape
import com.shub39.dharmik.shared.ui.endItemShape
import com.shub39.dharmik.shared.ui.leadingItemShape
import com.shub39.dharmik.shared.ui.listItemColors
import com.shub39.dharmik.shared.ui.middleItemShape
import com.shub39.dharmik.shared.ui.presentation.components.ListSelect
import com.shub39.dharmik.shared.ui.presentation.components.PageFill
import com.shub39.dharmik.shared.ui.presentation.components.SettingSlider
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToChangelog: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateBack: () -> Unit,
) =
    PageFill(modifier = modifier.background(colorScheme.background)) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val listState = rememberLazyListState()
        val uriHandler = LocalUriHandler.current

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeFlexibleTopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.settings),
                            fontFamily = getTitleFont(),
                        )
                    },
                    navigationIcon = {
                        FilledTonalIconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.arrow_back),
                                contentDescription = stringResource(Res.string.navigate_back),
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = colorScheme.surface
                        ),
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                contentPadding =
                    PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                        start =
                            paddingValues.calculateStartPadding(LocalLayoutDirection.current) +
                                8.dp,
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 8.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Dharmik Plus
                item {
                    ListItem(
                        colors = listItemColors(),
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.favorite),
                                contentDescription = null,
                                tint = colorScheme.primary,
                            )
                        },
                        headlineContent = { Text(text = stringResource(Res.string.dharmik_plus)) },
                        modifier =
                            Modifier.clip(detachedItemShape()).clickable { onNavigateToPaywall() },
                    )
                }

                // Appearance: language, theme and reading font size
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Column(modifier = Modifier.clip(leadingItemShape())) {
                            ListItem(
                                colors = listItemColors(),
                                leadingContent = {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.translate),
                                        contentDescription = null,
                                    )
                                },
                                headlineContent = {
                                    Text(text = stringResource(Res.string.verse_state))
                                },
                                supportingContent = {
                                    Text(text = stringResource(Res.string.verse_state_desc))
                                },
                            )

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .background(listItemColors().containerColor)
                                        .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                            ) {
                                ListSelect(
                                    title = null,
                                    options = VerseCardState.entries,
                                    selected = state.verseCardState,
                                    onSelectedChange = {
                                        onAction(SettingsAction.OnSetVerseCardState(it))
                                    },
                                    labelProvider = { Text(text = it.fullName) },
                                )
                            }
                        }

                        Column(modifier = Modifier.clip(middleItemShape())) {
                            ListItem(
                                colors = listItemColors(),
                                leadingContent = {
                                    Icon(
                                        imageVector =
                                            vectorResource(
                                                when (state.theme.appTheme) {
                                                    AppTheme.SYSTEM ->
                                                        if (isSystemInDarkTheme())
                                                            Res.drawable.dark_mode
                                                        else Res.drawable.light_mode

                                                    AppTheme.DARK -> Res.drawable.dark_mode
                                                    AppTheme.LIGHT -> Res.drawable.light_mode
                                                }
                                            ),
                                        contentDescription = null,
                                    )
                                },
                                headlineContent = {
                                    Text(text = stringResource(Res.string.app_theme))
                                },
                                supportingContent = {
                                    Text(text = stringResource(Res.string.app_theme_desc))
                                },
                            )

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .background(listItemColors().containerColor)
                                        .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                            ) {
                                ListSelect(
                                    title = null,
                                    options = AppTheme.entries,
                                    selected = state.theme.appTheme,
                                    onSelectedChange = {
                                        onAction(SettingsAction.OnSetAppTheme(it))
                                    },
                                    labelProvider = { Text(text = stringResource(it.labelRes())) },
                                )
                            }
                        }

                        Column(
                            modifier =
                                Modifier.clip(endItemShape())
                                    .background(listItemColors().containerColor)
                                    .padding(16.dp)
                        ) {
                            SettingSlider(
                                title = stringResource(Res.string.verses_font_size),
                                value = state.fontSize,
                                valueRange = 12f..24f,
                                onValueChange = { onAction(SettingsAction.SetFontSize(it)) },
                            )
                        }
                    }
                }

                // Offline audio
                item {
                    SegmentedListItem(
                        colors = listItemColors(),
                        selected =
                            state.bulkAudio is BulkAudioState.Downloading ||
                                state.bulkAudio is BulkAudioState.Checking ||
                                state.bulkAudio is BulkAudioState.Extracting,
                        leadingContent = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.download_for_offline),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            when (state.bulkAudio) {
                                Extracting -> {
                                    onAction(SettingsAction.OnCancelAudioDownload)
                                }
                                is Downloaded -> {
                                    onAction(SettingsAction.OnDeleteAudio)
                                }
                                is Downloading -> {
                                    onAction(SettingsAction.OnCancelAudioDownload)
                                }
                                else -> {
                                    onAction(SettingsAction.OnDownloadAudio)
                                }
                            }
                        },
                        shapes = segmentedListItemShapes(0, 1),
                        content = { Text(text = stringResource(Res.string.offline_audio)) },
                        supportingContent = {
                            Text(
                                text = bulkAudioText(state.bulkAudio),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        trailingContent = {
                            when (state.bulkAudio) {
                                is Downloading -> {
                                    CircularWavyProgressIndicator(
                                        progress = { state.bulkAudio.progress }
                                    )
                                }

                                is Extracting,
                                Checking -> {
                                    LoadingIndicator()
                                }

                                is Downloaded -> {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.delete),
                                        contentDescription = null,
                                    )
                                }

                                is NotDownloaded -> {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.download),
                                        contentDescription = null,
                                    )
                                }

                                is BulkAudioState.Failed -> {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.retry),
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                    )
                }

                // Data attribution links
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.github),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.open_link),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = { Text(text = "Dharmic Data") },
                            supportingContent = { Text(text = "by bhavyakhatri") },
                            modifier =
                                Modifier.clip(leadingItemShape()).clickable {
                                    uriHandler.openUri("https://github.com/bhavykhatri/DharmicData")
                                },
                        )

                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.globe),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.open_link),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = { Text(text = "Gita Supersite") },
                            supportingContent = { Text(text = "by IIT Kanpur") },
                            modifier =
                                Modifier.clip(endItemShape()).clickable {
                                    uriHandler.openUri("https://www.gitasupersite.iitk.ac.in/")
                                },
                        )
                    }
                }

                // About and Changelog
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.info),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = { Text(text = stringResource(Res.string.about)) },
                            supportingContent = {
                                Text(text = "Dharmik ${state.currentVersion ?: "x.x.x"}")
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.arrow_forward),
                                    contentDescription = "Navigate",
                                )
                            },
                            modifier =
                                Modifier.clip(leadingItemShape()).clickable { onNavigateToAbout() },
                        )

                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.check_list),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = { Text(text = stringResource(Res.string.changelog)) },
                            trailingContent = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.arrow_forward),
                                    contentDescription = "Navigate",
                                )
                            },
                            modifier =
                                Modifier.clip(endItemShape()).clickable { onNavigateToChangelog() },
                        )
                    }
                }
            }
        }
    }

@Composable
private fun bulkAudioText(state: BulkAudioState): String =
    when (state) {
        Checking -> stringResource(Res.string.audio_status_checking)

        is NotDownloaded ->
            stringResource(Res.string.audio_status_not_downloaded) + sizeSuffix(state.sizeBytes)

        is Downloading ->
            stringResource(Res.string.audio_status_downloading) +
                " ${(state.progress * 100).toInt()}%"

        Extracting -> stringResource(Res.string.audio_status_extracting)

        is Downloaded -> stringResource(Res.string.audio_status_downloaded)

        is Failed ->
            stringResource(Res.string.audio_status_failed) + (state.message?.let { ": $it" } ?: "")
    }

private fun sizeSuffix(sizeBytes: Long?): String =
    if (sizeBytes == null || sizeBytes <= 0L) "" else " (${sizeBytes / 1_000_000} MB)"

private fun AppTheme.labelRes(): StringResource =
    when (this) {
        LIGHT -> Res.string.light_theme
        DARK -> Res.string.dark_theme
        SYSTEM -> Res.string.system_theme
    }
