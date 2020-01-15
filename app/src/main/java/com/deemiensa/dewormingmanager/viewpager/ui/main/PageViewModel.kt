package com.deemiensa.dewormingmanager.viewpager.ui.main

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import com.deemiensa.dewormingmanager.offline.AppDatabase
import com.deemiensa.dewormingmanager.offline.DatabaseInfo
import com.deemiensa.dewormingmanager.offline.DatabaseOperations

class PageViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "history-list.db"
    ).build()

    internal val allData: LiveData<List<DatabaseInfo>> = db.databaseOperations().getAll()

    fun insert(databaseInfo: DatabaseInfo){
        db.databaseOperations().insertAll(databaseInfo)
    }
}