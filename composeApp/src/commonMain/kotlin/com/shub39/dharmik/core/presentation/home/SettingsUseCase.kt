package com.shub39.dharmik.core.presentation.home

import com.shub39.dharmik.core.domain.PreferencesRepo

class SettingsUseCase(
    private val preferencesRepo: PreferencesRepo
) {
    fun getPrefIsDarkTheme() = preferencesRepo.getIsDarkTheme()
    suspend fun setPrefIsDarkTheme(isDarkTheme: Boolean) {
        preferencesRepo.setIsDarkTheme(isDarkTheme)
    }
}