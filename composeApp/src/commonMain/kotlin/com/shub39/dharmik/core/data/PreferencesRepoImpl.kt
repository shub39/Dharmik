package com.shub39.dharmik.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.dharmik.core.domain.AppThemes
import com.shub39.dharmik.core.domain.PreferencesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepoImpl(
    private val dataStore: DataStore<Preferences>
): PreferencesRepo {

    companion object {
        const val DEFAULT_IS_DARK_THEME = false
    }

    private val isDarkThemeKey = booleanPreferencesKey("is_dark_theme")
    private val appThemeKey = stringPreferencesKey("app_theme")

    override fun getIsDarkTheme(): Flow<Boolean> = dataStore.data.map {
        it[isDarkThemeKey] ?: DEFAULT_IS_DARK_THEME
    }

    override suspend fun setIsDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { it[isDarkThemeKey] = isDarkTheme }
    }

    override fun getAppTheme(): Flow<AppThemes> = dataStore.data.map {
        when (it[appThemeKey]) {
            AppThemes.BLUE.name -> AppThemes.BLUE
            AppThemes.LIME.name -> AppThemes.LIME
            else -> AppThemes.YELLOW
        }
    }

    override suspend fun setAppTheme(appTheme: AppThemes) {
        dataStore.edit { it[appThemeKey] = appTheme.name }
    }
}