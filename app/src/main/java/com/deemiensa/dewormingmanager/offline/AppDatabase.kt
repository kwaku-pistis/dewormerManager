package com.deemiensa.dewormingmanager.offline

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DatabaseInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun databaseOperations(): DatabaseOperations
}