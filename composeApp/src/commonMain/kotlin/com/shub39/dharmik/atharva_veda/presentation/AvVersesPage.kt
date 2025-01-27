package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.core.presentation.components.ContentCap
import com.shub39.dharmik.core.presentation.components.scrollbar
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.kaanda_template
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvVersesPage(
    navController: NavController,
    state: AvState,
    action: (AvAction) -> Unit,
) = ContentCap {
    val scrollState = rememberLazyListState()

    val fontFamily = FontFamily(Font(Res.font.noto_regular))
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            Res.string.kaanda_template,
                            state.currentKaandas.first().kaanda
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .padding(padding)
                .scrollbar(
                    state = scrollState,
                    horizontal = false,
                    alignEnd = true,
                    thickness = 8.dp,
                    knobCornerRadius = 4.dp,
                    trackCornerRadius = 4.dp,
                    knobColor = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(state.currentKaandas, key = { it.sukta }) { kaanda ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = kaanda.text,
                            fontFamily = fontFamily
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "${kaanda.sukta} : ${kaanda.kaanda} || ${kaanda.samhita}"
                        )
                    },
                    trailingContent = {
                        var isFave by remember { mutableStateOf(state.favorites.contains(kaanda)) }

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
                                    action(AvAction.SetFave(kaanda))
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
        }
    }
}