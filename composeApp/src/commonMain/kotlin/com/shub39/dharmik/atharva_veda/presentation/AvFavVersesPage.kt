package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.shub39.dharmik.core.presentation.components.ContentCap
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvFavVersesPage(
    navController: NavController,
    state: AvState,
    action: (AvAction) -> Unit,
) = ContentCap {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { state.favorites.size }
    var sliderPosition by remember { mutableStateOf(0f) }

    val favoritesSize = state.favorites.size

    val fontFamily = FontFamily(Font(Res.font.noto_regular))
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(pagerState.currentPage) {
        sliderPosition = pagerState.currentPage.toFloat()
    }

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.atharva_veda)
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
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Previous"
                        )
                    }

                    Slider(
                        modifier = Modifier.weight(1f),
                        value = sliderPosition,
                        steps = when  {
                            favoritesSize > 100 -> (favoritesSize - 2) / 5
                            favoritesSize > 50 -> (favoritesSize - 2) / 3
                            else -> favoritesSize - 2
                        },
                        valueRange = 0f..state.favorites.size.toFloat().minus(1),
                        onValueChange = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(it.toInt())
                            }
                        }
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        enabled = pagerState.currentPage < state.favorites.size - 1
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
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding
        ) { index ->
            val currentVerse by remember { mutableStateOf(state.favorites[index]) }
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