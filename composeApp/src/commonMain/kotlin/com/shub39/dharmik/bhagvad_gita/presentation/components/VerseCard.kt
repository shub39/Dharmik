package com.shub39.dharmik.bhagvad_gita.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse
import com.shub39.dharmik.bhagvad_gita.presentation.removeExtraLineBreaks
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Clipboard
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.chapter_template
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.verses_alt_template
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerseCard(
    verse: GitaVerse,
    modifier: Modifier = Modifier,
    isFave: Boolean? = null,
    onFavorite: () -> Unit = {},
    onClick: () -> Unit = {},
    onCopy: () -> Unit = {}
) {
    Card(
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.chapter_template, verse.chapter),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(Res.string.verses_alt_template, verse.verse),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row {
                    isFave?.let {
                        IconButton(
                            onClick = onFavorite
                        ) {
                            Icon(
                                imageVector = if (isFave) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = "Favorite",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onCopy
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Clipboard,
                            contentDescription = "Copy",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Text(
                text = verse.text.removeExtraLineBreaks(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(Res.font.noto_regular))
            )
        }
    }
}