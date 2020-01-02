package com.deemiensa.dewormingmanager.viewpager

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.SharedPref
import com.deemiensa.dewormingmanager.pagerFragments.TakeMed
import com.deemiensa.dewormingmanager.viewpager.ui.main.SectionsPagerAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class Dewormer : AppCompatActivity() {

    private lateinit var dewormDate: EditText
    private var dateDewormerTaken: String? = null
    private var dialog_view: View? = null
    private lateinit var sharedPref: SharedPref

    @RequiresApi(Build.VERSION_CODES.O)
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
        val fab: FloatingActionButton = findViewById(R.id.fab)

        sharedPref = SharedPref(this)

        fab.setOnClickListener {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            // show a dialog for user to select date
            val dialog = MaterialDialog(this).title(text = "TAKE DEWORMER")
                .customView(R.layout.dialog_layout)
                .positiveButton(text = "DONE") { takeDewormDate() }
                .negativeButton(text = "CANCEL") {dialog -> dialog.dismiss()}

            dialog_view = dialog.getCustomView()

            dewormDate = dialog_view!!.findViewById(R.id.deworm_date)
            dewormDate.setOnClickListener { openCalenderDialog() }

            dialog.show()
        }
    }

    private fun takeDewormDate(){
        sharedPref.datetaken = dewormDate.text.toString()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = TakeMed()
        fragmentTransaction.detach(fragment)
        fragmentTransaction.attach(fragment)
        fragmentTransaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalenderDialog(){
        //val dialog = MaterialDialog.Builder(this).title("Date of deworming")
        MaterialDialog(this).show { datePicker { dialog, datetime ->
            val yr = datetime.year
            val month = datetime.month + 1
            val day = datetime.dayOfMonth

            val dayName = SimpleDateFormat("EEEE")
            val name = dayName.format(datetime.firstDayOfWeek)
            Log.d("DATE", name)

            dateDewormerTaken = "$day-$month-$yr"

            dewormDate.setText(datetime.time.toString())

//            val date = LocalDate.of(datetime.year, datetime.month + 1, datetime.dayOfMonth)
//            val nextDate = date.plusDays(90)
//
//            Log.d("NEXT DATE", nextDate.toString())
        }
        }
    }
}