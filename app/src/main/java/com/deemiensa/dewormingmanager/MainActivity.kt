package com.deemiensa.dewormingmanager

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var dateDewormerTaken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
}
