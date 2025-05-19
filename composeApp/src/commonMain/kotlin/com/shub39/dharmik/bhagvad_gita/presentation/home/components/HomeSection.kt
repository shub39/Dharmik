package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.presentation.components.VerseCard
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.core.presentation.components.simpleVerticalScrollbar
import com.shub39.dharmik.core.presentation.copyToClipboard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.Bookmark
import compose.icons.fontawesomeicons.solid.Heart
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.bookmark
import dharmik.composeapp.generated.resources.favorites_template
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeSection(
    onNavigateToVerses: () -> Unit,
    homeState: HomeState,
    onAction: (HomeAction) -> Unit
) = Box {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboard.current

    LazyColumn(
        state = listState,
        modifier = Modifier
            .simpleVerticalScrollbar(listState)
            .animateContentSize()
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.bookmark)
                    )
                },
                supportingContent = {
                    Text(text = "${homeState.currentBookMark.first} : ${homeState.currentBookMark.second.plus(1)}")
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            onAction(HomeAction.LoadBookMark)
                            onNavigateToVerses()
                        }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowRight,
                            contentDescription = "Open Bookmark",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Bookmark,
                        contentDescription = "Bookmark",
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        item {
            HorizontalDivider()
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.favorites_template, homeState.favorites.size))
                },
                leadingContent = {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Heart,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        items(homeState.favorites) { verse ->
            VerseCard(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                verse = verse,
                fontSize = homeState.fontSize,
                state = homeState.verseCardState,
                onClick = {
                    onAction(HomeAction.LoadVerse(verse))
                    onNavigateToVerses()
                },
                onCopy = {
                    coroutineScope.launch {
                        copyToClipboard(clipboardManager, it)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}