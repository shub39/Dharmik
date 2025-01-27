package com.shub39.dharmik.core.data

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DbConstructor: RoomDatabaseConstructor<DharmikDb> {
    override fun initialize(): DharmikDb
}