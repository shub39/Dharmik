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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.dharmik.shared.core.domain.GitaVerse
import com.shub39.dharmik.shared.core.domain.VerseCardState
import com.shub39.dharmik.shared.ui.presentation.removeExtraLineBreaks
import com.shub39.dharmik.shared.ui.theme.getHindiFont
import com.shub39.dharmik.shared.ui.theme.getSanskritFont
import dharmik.shared.ui.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerseCard(
    verse: GitaVerse,
    modifier: Modifier = Modifier,
    fontSize: Float = 16f,
    state: VerseCardState = VerseCardState.SANSKRIT,
) {
    Card(shape = MaterialTheme.shapes.large, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
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
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text =
                    when (state) {
                        ENGLISH -> verse.translations.shriPurohitSwami
                        HINDI -> verse.translations.swamiTejomayananda
                        SANSKRIT -> verse.text
                    }.removeExtraLineBreaks(),
                fontSize = fontSize.sp,
                lineHeight = fontSize.plus(3).sp,
                fontFamily =
                    when (state) {
                        ENGLISH,
                        HINDI -> getHindiFont()
                        SANSKRIT -> getSanskritFont()
                    },
            )
        }
    }
}
