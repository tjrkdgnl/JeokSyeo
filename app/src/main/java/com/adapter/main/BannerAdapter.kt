package com.adapter.main

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.adapter.viewholder.BannerViewHolder

class BannerAdapter(
    private val context: Context, private val itemList: MutableList<Int>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(itemList[position])
        if(position == itemList.size-2){
            viewPager2.post(addItemRunnable)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private var addItemRunnable = Runnable {
        itemList.addAll(itemList)
    }
}