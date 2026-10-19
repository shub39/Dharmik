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
package com.shub39.dharmik.shared.ui.util

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that requests the `POST_NOTIFICATIONS` runtime permission when invoked.
 *
 * The permission is only ever requested on demand (e.g. when a bulk audio download is started),
 * never at app launch. The outcome is reported through [onResult]:
 * - `true` when the permission was already granted, the platform is too old to need it, or the user
 *   granted it, so the caller can proceed.
 * - `false` when the user denied it.
 *
 * On Android (API 33+) this launches the system dialog the first time; on other targets it is a
 * no-op that always reports `true`.
 */
@Composable
expect fun rememberNotificationPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit
