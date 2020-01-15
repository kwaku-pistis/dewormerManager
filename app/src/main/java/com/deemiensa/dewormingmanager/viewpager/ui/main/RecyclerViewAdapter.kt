package com.deemiensa.dewormingmanager.viewpager.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.offline.DatabaseInfo
import kotlinx.android.synthetic.main.history_layout.view.*

class RecyclerViewAdapter(val databaseInfo: List<DatabaseInfo>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.history_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.id.text = databaseInfo[position].id.toString()
        holder.date.text = databaseInfo[position].date_taken
    }

    override fun getItemCount(): Int {
        return databaseInfo.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val id = itemView.id_tv
        val date = itemView.date_tv
    }
}