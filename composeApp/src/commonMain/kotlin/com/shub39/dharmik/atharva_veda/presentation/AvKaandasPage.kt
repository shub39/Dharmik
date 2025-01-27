package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.kaanda_template
import dharmik.composeapp.generated.resources.verses_template
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvKaandasPage(
    navController: NavController,
    state: AvState,
    action: (AvAction) -> Unit
) = ContentCap {
    val scrollState = rememberLazyListState()

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
        Box(
            modifier = Modifier.padding(padding)
        ) {
            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(state.kaandas.entries.toList(), key = { it.key }) { kaanda ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.kaanda_template, kaanda.key)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(Res.string.verses_template, kaanda.value.size)
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    action(AvAction.SetKaandas(kaanda.value))
                                    navController.navigate(Routes.AvVersesPage)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                    contentDescription = "Navigate"
                                )
                            }
                        }
                    )

                    HorizontalDivider()
                }

                item {
                    Spacer(modifier = Modifier.padding(60.dp))
                }
            }
        }
    }
}