package com.shub39.dharmik.core.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepo {
    fun getAppTheme(): Flow<AppTheme>
    suspend fun setAppTheme(appTheme: AppTheme)

    fun getBgBookMark(): Flow<LongPair>
    suspend fun setBgBookMark(mark: LongPair)

    fun getVerseCardState(): Flow<VerseCardState>
    suspend fun setVerseCardState(state: VerseCardState)

    fun getFontSize(): Flow<Float>
    suspend fun setFontSize(size: Float)
}