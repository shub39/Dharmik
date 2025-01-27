package com.shub39.dharmik.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shub39.dharmik.atharva_veda.data.AvConverters
import com.shub39.dharmik.atharva_veda.data.AvDao
import com.shub39.dharmik.atharva_veda.data.AvEntity

@Database(
    entities = [AvEntity::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(AvConverters::class)
abstract class DharmikDb: RoomDatabase() {
    abstract val avDao: AvDao

    companion object {
        const val DB_NAME = "dharmik.db"
    }
}