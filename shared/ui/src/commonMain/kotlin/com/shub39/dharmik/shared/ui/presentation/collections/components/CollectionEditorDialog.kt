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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.presentation.collections.CollectionEditor
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.cancel
import dharmik.shared.ui.generated.resources.collection_description
import dharmik.shared.ui.generated.resources.collection_name
import dharmik.shared.ui.generated.resources.create
import dharmik.shared.ui.generated.resources.edit_collection
import dharmik.shared.ui.generated.resources.new_collection
import dharmik.shared.ui.generated.resources.save
import org.jetbrains.compose.resources.stringResource

/** Creates a collection when [editor] has no id, renames it otherwise. */
@Composable
fun CollectionEditorDialog(
    editor: CollectionEditor,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isNew = editor.id == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text =
                    stringResource(
                        if (isNew) Res.string.new_collection else Res.string.edit_collection
                    )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editor.name,
                    onValueChange = onNameChange,
                    label = { Text(text = stringResource(Res.string.collection_name)) },
                    singleLine = true,
                )

                OutlinedTextField(
                    value = editor.description,
                    onValueChange = onDescriptionChange,
                    label = { Text(text = stringResource(Res.string.collection_description)) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = editor.name.isNotBlank()) {
                Text(text = stringResource(if (isNew) Res.string.create else Res.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
        },
    )
}
