package com.shub39.dharmik.bhagvad_gita.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.app.Routes
import com.shub39.dharmik.core.presentation.components.PageFill
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.bhagvad_gita
import dharmik.composeapp.generated.resources.chapter_template
import dharmik.composeapp.generated.resources.round_library_books_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BgChaptersPage(
    navController: NavController,
    state: BgState,
    action: (BgAction) -> Unit
) = PageFill {
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

        LazyVerticalGrid(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            columns = GridCells.Adaptive(180.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items((1..state.chapters).toList(), key = { it }) { no ->
                ListItem(
                    modifier = Modifier.clickable {
                        action(BgAction.ChapterChange(no))
                        navController.navigate(Routes.BgChapterVersesPage)
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.round_library_books_24),
                            contentDescription = "Book"
                        )
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.chapter_template, no)
                        )
                    }
                )
            }
        }
    }
}