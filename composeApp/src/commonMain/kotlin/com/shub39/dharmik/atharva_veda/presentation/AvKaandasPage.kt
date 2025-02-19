package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.kaanda_template
import dharmik.composeapp.generated.resources.round_library_books_24
import dharmik.composeapp.generated.resources.verses_template
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvKaandasPage(
    navController: NavController,
    state: AvState,
    action: (AvAction) -> Unit
) = PageFill {
    val scrollState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            MediumTopAppBar(
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
        }
    ) { padding ->
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Adaptive(180.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(state.kaandas.entries.toList(), key = { it.key }) { kaanda ->
                ListItem(
                    modifier = Modifier.clickable {
                        action(AvAction.SetKaandas(kaanda.value))
                        navController.navigate(Routes.AvVersesPage)
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.round_library_books_24),
                            contentDescription = "Book"
                        )
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.kaanda_template, kaanda.key)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.verses_template, kaanda.value.size)
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.padding(60.dp))
            }
        }
    }
}