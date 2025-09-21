package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
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
            .animateContentSize()
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items((1..state.chapters).toList(), key = { it }) { chapter ->
            ListItem(
                modifier = Modifier
                    .clickable {
                        onAction(HomeAction.ChapterChange(chapter))
                        onNavigateToVerses()
                    },
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.chapter_template, chapter)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Book,
                        contentDescription = "Chapter",
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = "Open Chapter",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}