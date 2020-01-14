package com.deemiensa.dewormingmanager.offline

import android.provider.BaseColumns

class DatabaseInfo internal constructor(){

    internal abstract class DatabaseVariables : BaseColumns{

        companion object{
            const val DATABASE_NAME = "offline_storage"

            const val TABLE_DEWORM = "table_deworm"
            const val ID =  "id"
            const val DATE_TAKEN = "date_taken"
            const val NEXT_DATE = "next_date"
        }
    }
}