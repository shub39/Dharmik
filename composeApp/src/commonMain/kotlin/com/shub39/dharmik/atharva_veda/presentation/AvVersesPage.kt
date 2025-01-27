package com.shub39.dharmik.atharva_veda.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.core.presentation.components.ContentCap
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.kaanda_template
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvVersesPage(
    navController: NavController,
    state: AvState,
    action: (AvAction) -> Unit,
) = ContentCap {
    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.kaanda_template, state.currentKaandas.first().kaanda)
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(state.currentKaandas, key = { it.sukta }) { kaanda ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = kaanda.text
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "${kaanda.sukta} || ${kaanda.kaanda} || ${kaanda.samhita}"
                        )
                    }
                )

                HorizontalDivider()
            }
        }
    }
}