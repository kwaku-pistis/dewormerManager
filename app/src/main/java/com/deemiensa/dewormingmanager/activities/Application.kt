package com.deemiensa.dewormingmanager.activities

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid


class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}
