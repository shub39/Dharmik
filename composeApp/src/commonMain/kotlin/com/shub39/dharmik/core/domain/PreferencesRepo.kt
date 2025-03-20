package com.shub39.dharmik.core.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepo {
    fun getAppTheme(): Flow<AppTheme>
    suspend fun setAppTheme(appTheme: AppTheme)

    fun getBgBookMark(): Flow<LongPair>
    suspend fun setBgBookMark(mark: LongPair)
}