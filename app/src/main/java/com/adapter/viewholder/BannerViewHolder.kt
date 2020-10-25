package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.bumptech.glide.Glide
import com.vuforia.engine.wet.BR
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.BannerItemBinding

class BannerViewHolder(parent:ViewGroup):BaseViewHolder<Int,BannerItemBinding>(R.layout.banner_item,parent) {

    override fun bind(data: Int) {
        Glide.with(binding.root)
            .load(data)
            .into(binding.bannerImg)
    }
}