package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.core.domain.IntPair
import com.shub39.dharmik.core.presentation.components.PageFill
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.kaanda_template
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvVersesPage(
    navController: NavController,
    state: AvState,
    action: (AvAction) -> Unit,
    favorites: Boolean = false
) = PageFill {
    val coroutineScope = rememberCoroutineScope()
    var sliderPosition by remember { mutableStateOf(state.pagerState.currentPage.toFloat()) }

    val fontFamily = FontFamily(Font(Res.font.noto_regular))
    val clipboardManager = LocalClipboardManager.current

    val changeVerse = { index: Int ->
        coroutineScope.launch {
            state.pagerState.animateScrollToPage(index)
        }

        if (!favorites) {
            action(AvAction.SetBookMark(
                IntPair(state.currentKaandas.first().kaanda, index)
            ))
        }
    }

    LaunchedEffect(state.pagerState.currentPage) {
        sliderPosition = state.pagerState.currentPage.toFloat()
    }

    val kaandas = state.currentKaandas.size

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (favorites) {
                            stringResource(Res.string.atharva_veda)
                        } else {
                            stringResource(
                                Res.string.kaanda_template,
                                state.currentKaandas.first().kaanda
                            )
                        }
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
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                Row {
                    IconButton(
                        onClick = {
                           changeVerse(state.pagerState.currentPage - 1)
                        },
                        enabled = state.pagerState.currentPage > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Previous"
                        )
                    }

                    Slider(
                        modifier = Modifier.weight(1f),
                        value = sliderPosition,
                        steps = when {
                            kaandas > 100 -> (kaandas - 2) / 5
                            kaandas > 50 -> (kaandas - 2) / 3
                            else -> kaandas - 2
                        }.coerceAtLeast(0),
                        valueRange = 0f..kaandas.toFloat().minus(1),
                        onValueChange = {
                           changeVerse(it.toInt())
                        }
                    )

                    IconButton(
                        onClick = {
                            changeVerse(state.pagerState.currentPage + 1)
                        },
                        enabled = state.pagerState.currentPage < kaandas - 1
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    ) { padding ->

        HorizontalPager(
            state = state.pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false,
            contentPadding = padding
        ) { index ->
            val currentVerse by remember {
                mutableStateOf(
                    state.currentKaandas[index]
                )
            }
            val colors = CardDefaults.cardColors()

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = colors.containerColor,
                        trailingIconColor = colors.contentColor,
                        headlineColor = colors.contentColor,
                        supportingColor = colors.contentColor
                    ),
                    headlineContent = {
                        Text(
                            text = currentVerse.veda.uppercase()
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "${currentVerse.samhita} ${currentVerse.kaanda}:${currentVerse.sukta}"
                        )
                    },
                    trailingContent = {
                        var isFave by remember {
                            mutableStateOf(
                                state.favorites.contains(
                                    currentVerse
                                )
                            )
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(
                                        annotatedString = buildAnnotatedString {
                                            append(currentVerse.text)
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
                                    action(AvAction.SetFave(currentVerse))
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

                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = currentVerse.text,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

    }
}