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
package com.shub39.dharmik.shared.ui.presentation.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.core.domain.PlaybackState
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.chapter_template
import dharmik.shared.ui.generated.resources.play
import dharmik.shared.ui.generated.resources.queue
import dharmik.shared.ui.generated.resources.verses_alt_template
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The queue of the session, with the track that is playing highlighted. */
@Composable
fun PlaylistBottomSheet(
    playback: PlaybackState,
    onSelect: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val playlist = playback.playlist ?: return

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
            Text(
                text = stringResource(Res.string.queue),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = getTitleFont(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(20.dp)),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                itemsIndexed(items = playlist.tracks, key = { index, _ -> index }) { index, track ->
                    SegmentedListItem(
                        onClick = { onSelect(index) },
                        selected = index == playback.index,
                        shapes = segmentedListItemShapes(index, playlist.tracks.size),
                        colors =
                            ListItemDefaults.colors(containerColor = colorScheme.surfaceContainer),
                        leadingContent = {
                            if (index == playback.index) {
                                Icon(
                                    painter = painterResource(Res.drawable.play),
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                )
                            }
                        },
                        content = {
                            Text(
                                text = stringResource(Res.string.verses_alt_template, track.verse),
                                color =
                                    if (index == playback.index) colorScheme.primary
                                    else colorScheme.onSurface,
                            )
                        },
                        overlineContent = {
                            Text(text = stringResource(Res.string.chapter_template, track.chapter))
                        },
                    )
                }
            }
        }
    }
}
