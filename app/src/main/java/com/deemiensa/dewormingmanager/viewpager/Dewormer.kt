package com.deemiensa.dewormingmanager.viewpager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.deemiensa.dewormingmanager.activities.AlarmReceiver
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.SharedPref
import com.deemiensa.dewormingmanager.viewpager.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout

class Dewormer : AppCompatActivity() {
    private lateinit var sharedPref: SharedPref
    val requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dewormer)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = this.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_action_dashboard_white)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_action_history_white)
        tabs.tabIconTint = getColorStateList(R.color.colorWhite)

        sharedPref = SharedPref(this)
        cancelAlarm()
    }

    fun getAlarmIntent(): Intent {
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "FOO_ACTION"
        return intent
    }

    fun cancelAlarm() {
        val sender = PendingIntent.getBroadcast(this, requestCode, getAlarmIntent(), 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
}