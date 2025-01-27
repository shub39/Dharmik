package com.shub39.dharmik.core.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.shub39.dharmik.core.domain.Books
import org.jetbrains.compose.resources.stringResource

@Composable
fun LibrarySection(
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(Books.entries.toList(), key = { it.name }) { book ->
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(book.displayName)
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(book.description)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = { navController.navigate(book.route) }
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