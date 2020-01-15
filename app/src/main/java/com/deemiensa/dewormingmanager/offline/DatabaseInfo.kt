package com.deemiensa.dewormingmanager.offline

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class DatabaseInfo(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "date_taken") var date_taken: String,
    @ColumnInfo(name = "next_date") var next_date: String
)