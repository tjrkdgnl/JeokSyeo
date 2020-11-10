package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.recommend_alcohol.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RecommendAlcoholItemBinding

class RecommendAlcoholViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,RecommendAlcoholItemBinding>(R.layout.recommend_alcohol_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.recommendItem = data
        binding.executePendingBindings()

        data.isLiked?.let {
            if(it){
                binding.activtyMainLikeImg.setImageResource(R.mipmap.full_heart)
            }
        }

        data.review?.score?.let {
            binding.activityMainRecommendRatingBar.rating =it
        }
    }
}