package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.core.presentation.components.simpleVerticalScrollbar
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.BookOpen
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.chapter_template
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChaptersSection(
    onNavigateToVerses: () -> Unit,
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .simpleVerticalScrollbar(listState)
            .animateContentSize()
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items((1..state.chapters).toList(), key = { it }) { chapter ->
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.chapter_template, chapter)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.BookOpen,
                        contentDescription = "Chapter",
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            onAction(HomeAction.ChapterChange(chapter))
                            onNavigateToVerses()
                        }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowRight,
                            contentDescription = "Open Chapter",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}