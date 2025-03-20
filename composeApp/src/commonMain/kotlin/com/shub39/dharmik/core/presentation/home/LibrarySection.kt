package com.shub39.dharmik.core.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.shub39.dharmik.app.Routes

@Composable
fun LibrarySection(
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ListItem(
                headlineContent = {

                },
                supportingContent = {

                },
                trailingContent = {
                    IconButton(
                        onClick = { navController.navigate(Routes.BhagvadGitaGraph) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Navigate"
                        )
                    }
                }
            )

            HorizontalDivider()
        }
    }
}