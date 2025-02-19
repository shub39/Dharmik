package com.shub39.dharmik.core.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepo {
    fun getIsDarkTheme(): Flow<Boolean>
    suspend fun setIsDarkTheme(isDarkTheme: Boolean)

    fun getAvBookMark(): Flow<IntPair>
    suspend fun setAvBookMark(mark: IntPair)

    fun getBgBookMark(): Flow<LongPair>
    suspend fun setBgBookMark(mark: LongPair)
}