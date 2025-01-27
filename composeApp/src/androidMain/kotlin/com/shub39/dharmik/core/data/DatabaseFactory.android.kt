package com.shub39.dharmik.core.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<DharmikDb> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DharmikDb.DB_NAME)

        return Room.databaseBuilder(appContext, dbFile.absolutePath)
    }
}