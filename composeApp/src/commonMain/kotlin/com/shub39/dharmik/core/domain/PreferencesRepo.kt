package com.shub39.dharmik.core.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepo {
    fun getIsDarkTheme(): Flow<Boolean>
    suspend fun setIsDarkTheme(isDarkTheme: Boolean)

    fun getBgBookMark(): Flow<LongPair>
    suspend fun setBgBookMark(mark: LongPair)
}