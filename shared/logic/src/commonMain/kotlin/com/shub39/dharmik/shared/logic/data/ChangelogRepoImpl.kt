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
package com.shub39.dharmik.shared.logic.data

import com.shub39.dharmik.shared.core.domain.Changelog
import com.shub39.dharmik.shared.core.domain.ChangelogRepo
import dharmik.shared.logic.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.annotation.Single

@Single(binds = [ChangelogRepo::class])
class ChangelogRepoImpl : ChangelogRepo {
    private val json = Json { ignoreUnknownKeys = true }

    private val _changelog: MutableStateFlow<Changelog> = MutableStateFlow(emptyList())
    override val changelog: Flow<Changelog> = _changelog.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch { loadChangelog() }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadChangelog() {
        // The changelog is best-effort: keep the empty default if the bundled JSON is unreadable.
        val parsed =
            runCatching {
                    val rawJson = Res.readBytes("files/changelog.json").decodeToString()
                    json.decodeFromString<Changelog>(rawJson)
                }
                .getOrNull()

        if (parsed != null) _changelog.update { parsed }
    }
}
