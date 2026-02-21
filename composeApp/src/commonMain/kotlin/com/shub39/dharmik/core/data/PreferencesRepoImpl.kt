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
package com.shub39.dharmik.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.domain.LongPair
import com.shub39.dharmik.core.domain.PreferencesRepo
import com.shub39.dharmik.core.domain.VerseCardState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class PreferencesRepoImpl(private val dataStore: DataStore<Preferences>) : PreferencesRepo {

    companion object {
        private val appThemeKey = stringPreferencesKey("is_dark_theme")
        private val bgBookMarkKey = stringPreferencesKey("bg_bookmark")
        private val verseCardStateKey = stringPreferencesKey("verse_card_state")
        private val fontSizeKey = floatPreferencesKey("font_size")
    }

    override fun getAppTheme(): Flow<AppTheme> =
        dataStore.data.map {
            when (it[appThemeKey]) {
                AppTheme.LIGHT.name -> AppTheme.LIGHT
                AppTheme.DARK.name -> AppTheme.DARK
                else -> AppTheme.SYSTEM
            }
        }

    override suspend fun setAppTheme(appTheme: AppTheme) {
        dataStore.edit { it[appThemeKey] = appTheme.name }
    }

    override fun getBgBookMark(): Flow<LongPair> =
        dataStore.data.map {
            Json.decodeFromString(it[bgBookMarkKey] ?: Json.encodeToString(LongPair(1, 1)))
        }

    override suspend fun setBgBookMark(mark: LongPair) {
        dataStore.edit { it[bgBookMarkKey] = Json.encodeToString(mark) }
    }

    override fun getVerseCardState(): Flow<VerseCardState> =
        dataStore.data.map {
            when (it[verseCardStateKey]) {
                VerseCardState.ENGLISH.name -> VerseCardState.ENGLISH
                VerseCardState.HINDI.name -> VerseCardState.HINDI
                else -> VerseCardState.SANSKRIT
            }
        }

    override suspend fun setVerseCardState(state: VerseCardState) {
        dataStore.edit { it[verseCardStateKey] = state.name }
    }

    override fun getFontSize(): Flow<Float> = dataStore.data.map { it[fontSizeKey] ?: 16f }

    override suspend fun setFontSize(size: Float) {
        dataStore.edit { it[fontSizeKey] = size }
    }
}
