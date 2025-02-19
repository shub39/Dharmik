package com.shub39.dharmik.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.dharmik.core.domain.IntPair
import com.shub39.dharmik.core.domain.LongPair
import com.shub39.dharmik.core.domain.PreferencesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PreferencesRepoImpl(
    private val dataStore: DataStore<Preferences>
): PreferencesRepo {

    companion object {
        const val DEFAULT_IS_DARK_THEME = false
        private val isDarkThemeKey = booleanPreferencesKey("is_dark_theme")
        private val avBookMarkKey = stringPreferencesKey("av_bookmark")
        private val bgBookMarkKey = stringPreferencesKey("bg_bookmark")
    }

    override fun getIsDarkTheme(): Flow<Boolean> = dataStore.data.map {
        it[isDarkThemeKey] ?: DEFAULT_IS_DARK_THEME
    }
    override suspend fun setIsDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { it[isDarkThemeKey] = isDarkTheme }
    }

    override fun getAvBookMark(): Flow<IntPair> = dataStore.data.map {
        Json.decodeFromString(it[avBookMarkKey] ?: Json.encodeToString(IntPair(0,0)))
    }
    override suspend fun setAvBookMark(mark: IntPair) {
        dataStore.edit { it[avBookMarkKey] = Json.encodeToString(mark) }
    }

    override fun getBgBookMark(): Flow<LongPair> = dataStore.data.map {
        Json.decodeFromString(it[bgBookMarkKey] ?: Json.encodeToString(LongPair(0, 0)))
    }
    override suspend fun setBgBookMark(mark: LongPair) {
        dataStore.edit { it[bgBookMarkKey] = Json.encodeToString(mark) }
    }
}