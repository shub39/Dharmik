package com.shub39.dharmik.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.domain.LongPair
import com.shub39.dharmik.core.domain.PreferencesRepo
import com.shub39.dharmik.core.domain.VerseCardState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PreferencesRepoImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepo {

    companion object {
        private val appThemeKey = stringPreferencesKey("is_dark_theme")
        private val bgBookMarkKey = stringPreferencesKey("bg_bookmark")
        private val verseCardStateKey = stringPreferencesKey("verse_card_state")
    }

    override fun getAppTheme(): Flow<AppTheme> = dataStore.data.map {
        when (it[appThemeKey]) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }
    }

    override suspend fun setAppTheme(appTheme: AppTheme) {
        dataStore.edit { it[appThemeKey] = appTheme.name }
    }

    override fun getBgBookMark(): Flow<LongPair> = dataStore.data.map {
        Json.decodeFromString(it[bgBookMarkKey] ?: Json.encodeToString(LongPair(1, 1)))
    }

    override suspend fun setBgBookMark(mark: LongPair) {
        dataStore.edit { it[bgBookMarkKey] = Json.encodeToString(mark) }
    }

    override fun getVerseCardState(): Flow<VerseCardState> = dataStore.data.map {
        when (it[verseCardStateKey]) {
            VerseCardState.ENGLISH.name -> VerseCardState.ENGLISH
            VerseCardState.HINDI.name -> VerseCardState.HINDI
            else -> VerseCardState.SANSKRIT
        }
    }

    override suspend fun setVerseCardState(state: VerseCardState) {
        dataStore.edit { it[verseCardStateKey] = state.name }
    }

}