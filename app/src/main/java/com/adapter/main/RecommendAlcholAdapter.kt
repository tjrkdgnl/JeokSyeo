package com.adapter.main

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.RecommendAlcholViewHolder
import com.model.recommend_alchol.AlcholList

class RecommendAlcholAdapter(private val context: Context,
private var lst:MutableList<AlcholList>) : RecyclerView.Adapter<RecommendAlcholViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecommendAlcholViewHolder {
        return RecommendAlcholViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecommendAlcholViewHolder, position: Int) {
        holder.bind(lst.get(position))
    }

    override fun getItemCount(): Int {
        return lst.size ?:0
    }
}