package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.recommend_alcohol.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RecommendAlcoholItemBinding

/**
 * 추천 주류를 아이템을 보여주는 뷰홀더
 */
class RecommendAlcoholViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,RecommendAlcoholItemBinding>(R.layout.recommend_alcohol_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.recommendItem = data
        binding.executePendingBindings()

        //추천주류를 찜한 경우 표시
        data.isLiked?.let {
            if(it){
                binding.activtyMainLikeImg.setImageResource(R.mipmap.recommend_heart_full)
            }
        }

        //추천 주류에 대한 점수 셋팅
        data.review?.score?.let {
            binding.activityMainRecommendRatingBar.rating =it
        }

        //해당 주류를 찜한 유저의 수 셋팅
        data.alcoholLikeCount?.let {
            binding.activityMainLikeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }
}