package com.deemiensa.dewormingmanager.offline

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseOperations{
    @Query("SELECT * from history ORDER BY id DESC")
    fun getAll(): LiveData<List<DatabaseInfo>>

    @Query("SELECT * FROM history WHERE date_taken LIKE :date")
    fun findByDate(date: String): DatabaseInfo

    @Insert
    fun insertAll(vararg todo: DatabaseInfo)

    @Delete
    fun delete(todo: DatabaseInfo)

    @Update
    fun updateHistory(vararg todos: DatabaseInfo)
}