package com.adapter.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlcholRankViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().rankingText.text = (position+1).toString()
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}