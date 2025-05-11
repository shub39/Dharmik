package com.shub39.dharmik.core.data

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<DharmikDb> {
        val os = System.getProperty("os.name").lowercase()
        val useHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Dharmik")
            os.contains("mac") -> File(useHome, "Library/Application Support/Dharmik")
            else -> File(useHome, ".local/share/Dharmik")
        }
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        val dbFile = File(appDataDir, DharmikDb.DB_NAME)

        return Room.databaseBuilder(dbFile.absolutePath)
    }
}