/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.ui.presentation.collections.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.cancel
import dharmik.shared.ui.generated.resources.notes
import dharmik.shared.ui.generated.resources.save
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoteEditorDialog(
    notes: String,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.notes)) },
        text = {
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text(text = stringResource(Res.string.notes)) },
                minLines = 3,
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text(text = stringResource(Res.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
        },
    )
}
