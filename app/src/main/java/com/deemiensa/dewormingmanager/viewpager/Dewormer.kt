package com.deemiensa.dewormingmanager.viewpager

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.SharedPref
import com.deemiensa.dewormingmanager.viewpager.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class Dewormer : AppCompatActivity() {

    private lateinit var dewormDate: EditText
    private var dateDewormerTaken: String? = null
    private var dialog_view: View? = null
    private lateinit var sharedPref: SharedPref

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

        sharedPref = SharedPref(this)
    }
}