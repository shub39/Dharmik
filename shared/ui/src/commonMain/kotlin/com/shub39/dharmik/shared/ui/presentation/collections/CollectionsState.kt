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
package com.shub39.dharmik.shared.ui.presentation.collections

import com.shub39.dharmik.shared.core.domain.Collections

data class CollectionsState(
    val collections: List<Collections> = emptyList(),
    val isPlus: Boolean = false,
    /** The collection being created or renamed, `null` when the dialog is closed. */
    val editor: CollectionEditor? = null,
)

/**
 * Backs the create/rename dialog. [id] is `null` while creating, so the same dialog serves both
 * flows.
 */
data class CollectionEditor(
    val id: Long? = null,
    val name: String = "",
    val description: String = "",
)
