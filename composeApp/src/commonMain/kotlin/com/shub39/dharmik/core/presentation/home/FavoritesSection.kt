package com.shub39.dharmik.core.presentation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.atharva_veda.presentation.AvAction
import com.shub39.dharmik.atharva_veda.presentation.AvState
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.liked_placeholder
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavoritesSection(
    avState: AvState,
    avAction: (AvAction) -> Unit,
) = Box {
    val scrollState = rememberLazyListState()

    val fontFamily = FontFamily(Font(Res.font.noto_regular))
    val clipboardManager = LocalClipboardManager.current

    if (avState.favorites.isNotEmpty()) {
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .animateContentSize()
            .padding(end = 12.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(avState.favorites, key = { it.text }) { kaanda ->
            ListItem(
                headlineContent = {
                    Text(
                        text = kaanda.text,
                        fontFamily = fontFamily
                    )
                },
                supportingContent = {
                    Text(
                        text = "${kaanda.sukta} : ${kaanda.kaanda} || ${kaanda.samhita} || ${kaanda.veda}"
                    )
                },
                trailingContent = {
                    var isFave by remember { mutableStateOf(avState.favorites.contains(kaanda)) }

                    Column {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(
                                    annotatedString = buildAnnotatedString {
                                        append(kaanda.text)
                                    }
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.round_content_copy_24),
                                contentDescription = "Copy to clipboard"
                            )
                        }

                        IconButton(
                            onClick = {
                                avAction(AvAction.SetFave(kaanda))
                                isFave = !isFave
                            }
                        ) {
                            Icon(
                                imageVector = if (isFave) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        }
                    }
                }
            )

            HorizontalDivider()
        }

        item {
            if (avState.favorites.isEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(200.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Text(
                        text = stringResource(Res.string.liked_placeholder),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}