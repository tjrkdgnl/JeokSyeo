package com.adapter.alcholdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholComponentViewHolder
import com.model.alchol_detail.AlcholComponentData

class AlcholComponentAdapter(val lst:MutableList<AlcholComponentData>) :RecyclerView.Adapter<AlcholComponentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholComponentViewHolder {
        return AlcholComponentViewHolder((parent))
    }

    override fun onBindViewHolder(holder: AlcholComponentViewHolder, position: Int) {
        holder.bind(lst[position])
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}