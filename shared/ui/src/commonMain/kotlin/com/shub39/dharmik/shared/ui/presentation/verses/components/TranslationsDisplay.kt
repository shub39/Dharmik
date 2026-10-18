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
import com.shub39.dharmik.shared.core.domain.Translations
import com.shub39.dharmik.shared.ui.segmentedListItemShapes
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.dropdown
import dharmik.shared.ui.generated.resources.dropup
import dharmik.shared.ui.generated.resources.translations
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TranslationsDisplay(translations: Translations, onCopy: (String) -> Unit) {
    var currentSelected by rememberSaveable { mutableStateOf("") }

    val translationsMap =
        mapOf(
            "Sri Harikrishnadas Goenka" to translations.sriHarikrishnadasGoenka,
            "Swami Ramsukhdas" to translations.swamiRamsukhdas,
            "Swami Tejomayananda" to translations.swamiTejomayananda,
            "Swami Adidevananda" to translations.swamiAdidevananda,
            "Swami Gambirananda" to translations.swamiGambirananda,
            "Swami Sivananda" to translations.swamiSivananda,
            "Dr. S Sankaranarayan" to translations.drSSankaranarayan,
            "Shri Purohit Swami" to translations.shriPurohitSwami,
        )

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(Res.string.translations),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )

        translationsMap.toList().forEachIndexed { index, (key, value) ->
            if (value.isNotEmpty()) {
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
                    shapes = segmentedListItemShapes(index, translationsMap.size),
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
