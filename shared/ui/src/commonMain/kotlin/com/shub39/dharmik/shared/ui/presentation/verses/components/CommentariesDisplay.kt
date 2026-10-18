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
package com.shub39.dharmik.shared.ui.presentation.verses.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.core.domain.Commentaries
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.commentaries
import dharmik.shared.ui.generated.resources.dropdown
import dharmik.shared.ui.generated.resources.dropup
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentariesDisplay(commentaries: Commentaries, onCopy: (String) -> Unit) {
    var currentSelected by rememberSaveable { mutableStateOf("") }

    val commentaryMap =
        mapOf(
            "Swami Ramsukhdas" to commentaries.swamiRamsukhdas,
            "Sri Harikrishnadas Goenka" to commentaries.sriHarikrishnadasGoenka,
            "Sri Anandgiri" to commentaries.sriAnandgiri,
            "Sri Dhanpati" to commentaries.sriDhanpati,
            "Sri Madhavacharya" to commentaries.sriMadhavacharya,
            "Sri Neelkanth" to commentaries.sriNeelkanth,
            "Sri Ramanuja" to commentaries.sriRamanuja,
            "Sri Sridhara Swami" to commentaries.sriSridharaSwami,
            "Sri Vedantadeshikacharya Venkatanatha" to
                commentaries.sriVedantadeshikacharyaVenkatanatha,
            "Swami Chinmayananda" to commentaries.swamiChinmayananda,
            "Sri Abhinavgupta" to commentaries.sriAbhinavgupta,
            "Sri Jayatritha" to commentaries.sriJayatritha,
            "Sri Madhusudan Saraswati" to commentaries.sriMadhusudanSaraswati,
            "Sri Purushottamji" to commentaries.sriPurushottamji,
            "Sri Shankaracharya" to commentaries.sriShankaracharya,
            "Sri Vallabhacharya" to commentaries.sriVallabhacharya,
            "Swami Sivananda" to commentaries.swamiSivananda,
            "Swami Gambirananda" to commentaries.swamiGambirananda,
            "Dr. S Sankaranarayan" to commentaries.drSSankaranarayan,
            "Swami Adidevananda" to commentaries.swamiAdidevananda,
        )

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(Res.string.commentaries),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )

        commentaryMap.toList().forEachIndexed { index, (key, value) ->
            if (!value.isNullOrEmpty()) {
                val isExpanded = currentSelected == key
                SegmentedListItem(
                    checked = isExpanded,
                    onCheckedChange = { currentSelected = if (isExpanded) "" else key },
                    onLongClick =
                        if (isExpanded) {
                            { onCopy(value) }
                        } else null,
                    onLongClickLabel = "Copy",
                    colors = ListItemDefaults.colors(containerColor = colorScheme.surfaceContainer),
                    shapes = segmentedListItemShapes(index, commentaryMap.size),
                    content = { Text(text = key) },
                    supportingContent = {
                        AnimatedVisibility(isExpanded) {
                            Text(text = value, fontFamily = getHindiFont())
                        }
                    },
                    trailingContent = {
                        Icon(
                            painter =
                                painterResource(
                                    if (isExpanded) {
                                        Res.drawable.dropup
                                    } else {
                                        Res.drawable.dropdown
                                    }
                                ),
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}
