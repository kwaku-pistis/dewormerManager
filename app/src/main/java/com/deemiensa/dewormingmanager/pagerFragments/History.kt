package com.deemiensa.dewormingmanager.pagerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.AppDatabase
import com.deemiensa.dewormingmanager.viewpager.ui.main.PageViewModel
import com.deemiensa.dewormingmanager.viewpager.ui.main.RecyclerViewAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class History : Fragment() {

    private lateinit var model: PageViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = root.findViewById(R.id.history_recyclerView)
        model = ViewModelProviders.of(activity!!).get(PageViewModel::class.java)

        // Specify layout for recycler view
        val linearLayoutManager = LinearLayoutManager(
            context, RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = linearLayoutManager

        // observe the model
        model.allData.observe(activity!!, Observer { data ->
            // data bind the recycler view
            recyclerView.adapter = RecyclerViewAdapter(data)
        })

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() = History()
    }
}
