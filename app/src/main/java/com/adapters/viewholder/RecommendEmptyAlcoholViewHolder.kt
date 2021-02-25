package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.recommend_alcohol.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RecommendAlcoholItemBinding

class RecommendEmptyAlcoholViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,RecommendAlcoholItemBinding>(R.layout.recommend_alcohol_empty_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.recommendItem = data
        binding.executePendingBindings()

        data.isLiked?.let {
            if(it){
                binding.activtyMainLikeImg.setImageResource(R.mipmap.detail_full_heart)
            }
        }

        data.review?.score?.let {
            binding.activityMainRecommendRatingBar.rating =it
        }

        data.alcoholLikeCount?.let {
            binding.activityMainLikeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }
}