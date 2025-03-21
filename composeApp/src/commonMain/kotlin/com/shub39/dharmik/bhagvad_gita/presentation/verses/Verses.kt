package com.shub39.dharmik.bhagvad_gita.presentation.verses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.shub39.dharmik.bhagvad_gita.presentation.removeExtraLineBreaks
import com.shub39.dharmik.bhagvad_gita.presentation.verses.components.CommentariesDisplay
import com.shub39.dharmik.bhagvad_gita.presentation.verses.components.TranslationsDisplay
import com.shub39.dharmik.core.domain.LongPair
import com.shub39.dharmik.core.presentation.components.PageFill
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.bhagvad_gita
import dharmik.composeapp.generated.resources.chapter_template
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Verses(
    navController: NavController,
    state: VersesState,
    action: (VersesAction) -> Unit,
    favorites: Boolean = false
) = PageFill {
    val fontFamily = FontFamily(Font(Res.font.noto_regular))
    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()
    var sliderPosition by remember { mutableStateOf(0f) }

    val changeVerse = {index: Int ->
        coroutineScope.launch {
            state.pagerState.animateScrollToPage(index)
        }

        if (!favorites) {
            action(
                VersesAction.SetIndex(
                LongPair(state.currentFile.first().chapter, index.toLong())
            ))
        }
    }

    val verses = state.currentFile

    LaunchedEffect(state.pagerState.currentPage) {
        sliderPosition = state.pagerState.currentPage.toFloat()
    }

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (favorites) {
                            stringResource(Res.string.bhagvad_gita)
                        } else {
                            stringResource(
                                Res.string.chapter_template,
                                verses.first().chapter
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
                            verses.size > 100 -> (verses.size - 2) / 5
                            verses.size > 50 -> (verses.size - 2) / 3
                            else -> verses.size - 2
                        }.coerceAtLeast(0),
                        valueRange = 0f..verses.size.toFloat().minus(1),
                        onValueChange = {
                            changeVerse(it.toInt())
                        }
                    )

                    IconButton(
                        onClick = {
                            changeVerse(state.pagerState.currentPage + 1)
                        },
                        enabled = state.pagerState.currentPage < verses.size - 1
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
            contentPadding = padding,
            userScrollEnabled = false
        ) { index ->
            val currentVerse by remember {
                mutableStateOf(verses[index])
            }
            val colors = CardDefaults.cardColors()
            val scrollState = rememberLazyListState()

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card {
                        ListItem(
                            colors = ListItemDefaults.colors(
                                containerColor = colors.containerColor,
                                trailingIconColor = colors.contentColor,
                                headlineColor = colors.contentColor,
                                supportingColor = colors.contentColor
                            ),
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.bhagvad_gita).uppercase()
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = "${currentVerse.chapter} : ${currentVerse.verse}"
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
                                            action(VersesAction.SetFave(currentVerse))
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

                        Text(
                            text = currentVerse.text.removeExtraLineBreaks(),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(30.dp))

                    TranslationsDisplay(
                        translations = currentVerse.translations,
                        onCopy = {
                            clipboardManager.setText(
                                annotatedString = buildAnnotatedString {
                                    append(it)
                                }
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.padding(30.dp))

                    CommentariesDisplay(
                        commentaries = currentVerse.commentaries,
                        onCopy = {
                            clipboardManager.setText(
                                annotatedString = buildAnnotatedString {
                                    append(it)
                                }
                            )
                        }
                    )
                }
            }
        }

    }
}