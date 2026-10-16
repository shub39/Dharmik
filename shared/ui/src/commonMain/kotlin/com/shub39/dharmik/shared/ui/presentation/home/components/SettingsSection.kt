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
package com.shub39.dharmik.shared.ui.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.core.domain.AppTheme
import com.shub39.dharmik.shared.core.domain.BulkAudioState
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.ui.presentation.components.SettingSlider
import com.shub39.dharmik.shared.ui.presentation.home.HomeAction
import com.shub39.dharmik.shared.ui.presentation.home.HomeState
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.app_theme
import dharmik.shared.ui.generated.resources.app_theme_desc
import dharmik.shared.ui.generated.resources.audio_cancel
import dharmik.shared.ui.generated.resources.audio_delete
import dharmik.shared.ui.generated.resources.audio_download
import dharmik.shared.ui.generated.resources.audio_status_checking
import dharmik.shared.ui.generated.resources.audio_status_downloaded
import dharmik.shared.ui.generated.resources.audio_status_downloading
import dharmik.shared.ui.generated.resources.audio_status_extracting
import dharmik.shared.ui.generated.resources.audio_status_failed
import dharmik.shared.ui.generated.resources.audio_status_not_downloaded
import dharmik.shared.ui.generated.resources.audio_status_update_available
import dharmik.shared.ui.generated.resources.dark_theme
import dharmik.shared.ui.generated.resources.github
import dharmik.shared.ui.generated.resources.globe
import dharmik.shared.ui.generated.resources.light_theme
import dharmik.shared.ui.generated.resources.offline_audio
import dharmik.shared.ui.generated.resources.system_theme
import dharmik.shared.ui.generated.resources.verse_state
import dharmik.shared.ui.generated.resources.verse_state_desc
import dharmik.shared.ui.generated.resources.verses_font_size
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsSection(state: HomeState, onAction: (HomeAction) -> Unit) {
    val listState = rememberLazyListState()
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SettingSlider(
                title = stringResource(Res.string.verses_font_size),
                value = state.fontSize,
                valueRange = 12f..24f,
                onValueChange = { onAction(HomeAction.SetFontSize(it)) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        item {
            ListItem(
                headlineContent = { Text(text = stringResource(Res.string.offline_audio)) },
                supportingContent = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = bulkAudioText(state.bulkAudio))

                        val bulkAudio = state.bulkAudio
                        if (bulkAudio is BulkAudioState.Downloading) {
                            LinearProgressIndicator(
                                progress = { bulkAudio.progress },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                },
                trailingContent = {
                    when (state.bulkAudio) {
                        is BulkAudioState.Downloading,
                        BulkAudioState.Extracting ->
                            TextButton(onClick = { onAction(HomeAction.OnCancelAudioDownload) }) {
                                Text(text = stringResource(Res.string.audio_cancel))
                            }

                        is BulkAudioState.Downloaded ->
                            TextButton(onClick = { onAction(HomeAction.OnDeleteAudio) }) {
                                Text(text = stringResource(Res.string.audio_delete))
                            }

                        else ->
                            Button(onClick = { onAction(HomeAction.OnDownloadAudio) }) {
                                Text(text = stringResource(Res.string.audio_download))
                            }
                    }
                },
            )
        }

        item {
            ListItem(
                headlineContent = { Text(text = stringResource(Res.string.verse_state)) },
                supportingContent = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stringResource(Res.string.verse_state_desc))

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                        ) {
                            VerseCardState.entries.forEach { vcState ->
                                ToggleButton(
                                    checked = vcState == state.verseCardState,
                                    onCheckedChange = {
                                        onAction(HomeAction.OnSetVerseCardState(vcState))
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(text = vcState.fullName)
                                }
                            }
                        }
                    }
                },
            )
        }

        item {
            ListItem(
                headlineContent = { Text(text = stringResource(Res.string.app_theme)) },
                supportingContent = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stringResource(Res.string.app_theme_desc))

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                        ) {
                            AppTheme.entries.forEach { appTheme ->
                                ToggleButton(
                                    checked = appTheme == state.theme.appTheme,
                                    onCheckedChange = {
                                        onAction(HomeAction.OnSetAppTheme(appTheme))
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(text = stringResource(appTheme.labelRes()))
                                }
                            }
                        }
                    }
                },
            )
        }

        item { HorizontalDivider() }

        item {
            ListItem(
                headlineContent = { Text(text = "Dharmic Data") },
                supportingContent = { Text(text = "by bhavyakhatri") },
                trailingContent = {
                    Row {
                        FilledTonalIconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/bhavykhatri/DharmicData")
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.github),
                                contentDescription = "Github",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                },
            )
        }

        item {
            ListItem(
                headlineContent = { Text(text = "Gita Supersite") },
                supportingContent = { Text(text = "by IIT Kanpur") },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = { uriHandler.openUri("https://www.gitasupersite.iitk.ac.in/") }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.globe),
                            contentDescription = "Open In Browser",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun bulkAudioText(state: BulkAudioState): String =
    when (state) {
        BulkAudioState.Checking -> stringResource(Res.string.audio_status_checking)

        is BulkAudioState.NotDownloaded ->
            stringResource(Res.string.audio_status_not_downloaded) + sizeSuffix(state.sizeBytes)

        is BulkAudioState.UpdateAvailable ->
            stringResource(Res.string.audio_status_update_available) + sizeSuffix(state.sizeBytes)

        is BulkAudioState.Downloading ->
            stringResource(Res.string.audio_status_downloading) +
                " ${(state.progress * 100).toInt()}%"

        BulkAudioState.Extracting -> stringResource(Res.string.audio_status_extracting)

        is BulkAudioState.Downloaded -> stringResource(Res.string.audio_status_downloaded)

        is BulkAudioState.Failed -> stringResource(Res.string.audio_status_failed)
    }

private fun sizeSuffix(sizeBytes: Long?): String =
    if (sizeBytes == null || sizeBytes <= 0L) "" else " (${sizeBytes / 1_000_000} MB)"

private fun AppTheme.labelRes(): StringResource =
    when (this) {
        AppTheme.LIGHT -> Res.string.light_theme
        AppTheme.DARK -> Res.string.dark_theme
        AppTheme.SYSTEM -> Res.string.system_theme
    }
