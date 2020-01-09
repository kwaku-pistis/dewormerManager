package com.deemiensa.dewormingmanager.offline

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPref @SuppressLint("CommitPrefEdits")
constructor (context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    // initialize the pref manager variables
    init {
        val privateMode = 0
        pref = context.getSharedPreferences(PREF_NAME, privateMode)
        editor = pref.edit()
    }

    var datetaken: String?
        get() = pref.getString("date_taken", "")
        set(value) = editor.putString("date_taken", value).apply()

    var lastdatetaken: String?
        get() = pref.getString("last_taken", "")
        set(value) = editor.putString("last_taken", value).apply()

    var unFormattedNextDate: String?
        get() = pref.getString("unf_next_date", "")
        set(value) = editor.putString("unf_next_date", value).apply()

    var nextDate: String?
        get() = pref.getString("next_date", "")
        set(value) = editor.putString("next_date", value).apply()

    companion object{
        private const val PREF_NAME = "Deworming Manager"
    }
}