package com.adapters.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapters.viewholder.BannerViewHolder
import com.model.banner.Banner

class BannerAdapter(
    private val context: Context, private val itemList: MutableList<Banner>
) : RecyclerView.Adapter<BannerViewHolder>() {
    private var position =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(itemList[position])
        this.position = position

        //배너 클릭 시, 연결된 url로 이동
        holder.getViewBinding().bannerParentLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemList[position].url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}