package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.recommend_alchol.AlcholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RecommendAlcholItemBinding

class RecommendAlcholViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcholList,RecommendAlcholItemBinding>(R.layout.recommend_alchol_item,parent) {

    override fun bind(data: AlcholList) {
        binding.recommendItem = data
        binding.executePendingBindings()

        data.isLiked?.let {
            if(it){
                binding.activtyMainLikeImg.setImageResource(R.mipmap.full_heart)
            }
        }
    }
}