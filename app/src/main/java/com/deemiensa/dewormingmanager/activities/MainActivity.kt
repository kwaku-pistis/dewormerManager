package com.deemiensa.dewormingmanager.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.deemiensa.dewormingmanager.R
import com.google.android.material.tabs.TabLayout

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var dateDewormerTaken: String? = null
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // initializing the viewpager
        viewPager = findViewById(R.id.viewpager_main)
        tabs = findViewById(R.id.profile_tabs)

        // setting up the viewpager adapter
//        val fragmentAdapter = ViewPagerAdapter(childFragmentManager)
//        viewPager.adapter = fragmentAdapter
//        tabs.setupWithViewPager(viewPager)

        fab.setOnClickListener {openCalenderDialog()}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openCalenderDialog(){
        //val dialog = MaterialDialog.Builder(this).title("Date of deworming")
        MaterialDialog(this).show { datePicker { dialog, datetime ->
                val yr = datetime.year
                val month = datetime.month
                val day = datetime.dayOfMonth

                dateDewormerTaken = "$day-$month-$yr"

                Log.d("DATE", dateDewormerTaken)
            }
        }
    }


    private fun triggerAlarm(){
        // Get AlarmManager instance
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent part
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Medium AlarmManager Demo")

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        // Alarm time
        val ALARM_DELAY_IN_SECOND = 10
        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L

        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
    }
}
