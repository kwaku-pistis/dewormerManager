package com.deemiensa.dewormingmanager.offline

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOperations(context: Context) : SQLiteOpenHelper(context, DatabaseInfo.DatabaseVariables.DATABASE_NAME, null, database_version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        private const val database_version = 1
    }
}