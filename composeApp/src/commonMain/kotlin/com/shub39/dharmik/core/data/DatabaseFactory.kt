package com.shub39.dharmik.core.data

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<DharmikDb>
}