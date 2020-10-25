package com.adapter.main

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholRankViewHolder
import com.model.alchol_ranking.AlcholList

class AlcholRankAdapter(
    private val context: Context,
    private val lst:MutableList<AlcholList>
) : RecyclerView.Adapter<AlcholRankViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholRankViewHolder {
        return AlcholRankViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholRankViewHolder, position: Int) {
        holder.bind(lst[position])
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}