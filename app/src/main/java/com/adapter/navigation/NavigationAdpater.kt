package com.adapter.navigation

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.NavigationViewHolder
import com.model.navigation.NavigationItem

class NavigationAdpater(
    private val context: Context ,
    private val lst:MutableList<NavigationItem>) : RecyclerView.Adapter<NavigationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.bind(lst[position])
    }

    override fun getItemCount(): Int{
        return lst.size
    }
}