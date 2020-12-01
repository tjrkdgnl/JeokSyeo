package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.banner.Banner
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.BannerItemBinding

class BannerViewHolder(parent:ViewGroup):BaseViewHolder<Banner,BannerItemBinding>(R.layout.banner_item,parent) {

    override fun bind(data: Banner) {
        binding.banner = data
        binding.executePendingBindings()
    }
}