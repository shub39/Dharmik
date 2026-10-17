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
package com.shub39.dharmik.shared.ui.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.ui.presentation.removeExtraLineBreaks
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import com.shub39.dharmik.shared.ui.theme.getSanskritFont
import dharmik.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerseCard(
    verse: GitaVerse,
    modifier: Modifier = Modifier,
    fontSize: Float = 16f,
    state: VerseCardState = VerseCardState.SANSKRIT,
    isFave: Boolean? = null,
    onToggleFave: () -> Unit = {},
    onClick: () -> Unit = {},
    onCopy: (String) -> Unit = {},
    playIcon: @Composable () -> Unit = {},
) {
    Card(shape = MaterialTheme.shapes.large, onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.chapter_template, verse.chapter),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = stringResource(Res.string.verses_alt_template, verse.verse),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Row {
                    isFave?.let {
                        IconButton(onClick = onToggleFave) {
                            Icon(
                                painter =
                                    painterResource(
                                        if (it) Res.drawable.favorite
                                        else Res.drawable.favorite_border
                                    ),
                                contentDescription = "Favorite",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onCopy(
                                when (state) {
                                    VerseCardState.ENGLISH -> verse.translations.shriPurohitSwami
                                    VerseCardState.HINDI -> verse.translations.swamiTejomayananda
                                    VerseCardState.SANSKRIT -> verse.text
                                }
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.content_copy),
                            contentDescription = "Copy",
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    playIcon()
                }
            }

            Text(
                text =
                    when (state) {
                        ENGLISH -> verse.translations.shriPurohitSwami
                        HINDI -> verse.translations.swamiTejomayananda
                        SANSKRIT -> verse.text
                    }.removeExtraLineBreaks(),
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSize.sp,
                        fontFamily =
                            when (state) {
                                ENGLISH,
                                HINDI -> getHindiFont()
                                SANSKRIT -> getSanskritFont()
                            },
                    ),
            )
        }
    }
}
