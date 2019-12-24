package com.deemiensa.dewormingmanager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.deemiensa.dewormingmanager.pagerFragments.History
import com.deemiensa.dewormingmanager.pagerFragments.TakeMed

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TakeMed()
            }
            else -> {
                return History()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Posts"
            else -> {
                return "Saved Posts"
            }
        }
    }
}