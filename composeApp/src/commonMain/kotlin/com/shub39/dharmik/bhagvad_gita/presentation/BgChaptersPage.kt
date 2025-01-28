package com.shub39.dharmik.bhagvad_gita.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.app.Routes
import com.shub39.dharmik.core.presentation.components.ContentCap
import com.shub39.dharmik.core.presentation.components.scrollbar
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.bhagvad_gita
import dharmik.composeapp.generated.resources.chapter_template
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BgChaptersPage(
    navController: NavController,
    state: BgState,
    action: (BgAction) -> Unit
) = ContentCap {
    val scrollState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.bhagvad_gita)
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
            items((1..state.chapters).toList(), key = { it }) { no ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.chapter_template, no)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                action(BgAction.ChapterChange(no))
                                navController.navigate(Routes.BgChapterVersesPage)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Navigate"
                            )
                        }
                    }
                )

                HorizontalDivider()
            }
        }

    }
}