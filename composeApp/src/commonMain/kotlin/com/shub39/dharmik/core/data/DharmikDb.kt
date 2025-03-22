package com.shub39.dharmik.core.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shub39.dharmik.bhagvad_gita.data.BgDao
import com.shub39.dharmik.bhagvad_gita.data.BgVerseEntity
import com.shub39.dharmik.bhagvad_gita.data.BgVerseTypeConverters

@Database(
    entities = [BgVerseEntity::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(BgVerseTypeConverters::class)
@ConstructedBy(DbConstructor::class)
abstract class DharmikDb: RoomDatabase() {
    abstract val bgDao: BgDao

    companion object {
        const val DB_NAME = "dharmik.db"
    }
}