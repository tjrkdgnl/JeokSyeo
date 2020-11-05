package com.adapter.alcholdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.DepthComponentViewholder

class DepthComponentAdapter(private val lst:MutableList<String>?) :RecyclerView.Adapter<DepthComponentViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepthComponentViewholder {
        return DepthComponentViewholder(parent)
    }

    override fun onBindViewHolder(holder: DepthComponentViewholder, position: Int) {
        lst?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
       return lst?.size ?:0
    }
}