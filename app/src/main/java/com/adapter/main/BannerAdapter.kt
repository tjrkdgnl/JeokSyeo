package com.adapter.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.adapter.viewholder.BannerViewHolder
import com.model.banner.Banner

class BannerAdapter(
    private val context: Context, private val itemList: MutableList<Banner>,
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

        holder.getViewBinding().bannerParentLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemList[position].url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private var addItemRunnable = Runnable {
        itemList.addAll(itemList)
    }
}