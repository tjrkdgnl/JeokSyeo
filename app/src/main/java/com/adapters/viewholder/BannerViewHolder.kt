package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.banner.Banner
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.BannerItemBinding

/**
 * 배너에 표시될 이미지 뷰홀더
 */
class BannerViewHolder(parent:ViewGroup):BaseViewHolder<Banner,BannerItemBinding>(R.layout.banner_item,parent) {

    override fun bind(data: Banner) {
        binding.banner = data
        binding.executePendingBindings()
    }
}