package com.shub39.dharmik.bhagvad_gita.presentation.verses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Clipboard
import compose.icons.fontawesomeicons.solid.Eye

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogPeek(
    title: String,
    content: String,
    onCopy: (String) -> Unit,
    fontSize: Float = 16f
) {
    var showComplete by remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.clickable { showComplete = true },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        },
        trailingContent = {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Eye,
                contentDescription = "Show",
                modifier = Modifier.size(20.dp)
            )
        }
    )

    if (showComplete) {
        ModalBottomSheet(
            onDismissRequest = { showComplete = false }
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    FilledTonalIconButton(
                        onClick = { onCopy(content) }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Clipboard,
                            contentDescription = "Copy",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = content,
                    fontSize = fontSize.sp,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                )

                Spacer(modifier = Modifier.padding(16.dp))
            }
        }
    }
}