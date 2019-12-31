package com.deemiensa.dewormingmanager.pagerFragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.SharedPref

/**
 * A simple [Fragment] subclass.
 */
class TakeMed : Fragment() {

    private lateinit var lastDate: TextView
    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_take_med, container, false)

        sharedPref = SharedPref(context!!)

        lastDate = root.findViewById(R.id.last_taken_tv_2)
        lastDate.text = sharedPref.datetaken

        return root
    }

    override fun onResume() {
        super.onResume()

        lastDate.text = sharedPref.datetaken
    }

    companion object {
        @JvmStatic
        fun newInstance() = TakeMed()
    }
}
