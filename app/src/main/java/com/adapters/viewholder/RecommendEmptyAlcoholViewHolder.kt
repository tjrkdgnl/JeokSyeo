package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.recommend_alcohol.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RecommendAlcoholItemBinding

/**
 * 네트워크 통신이 되지 않을 때, 기본적으로 추천주류에 표시되는 default 아이템 뷰홀더
 */
class RecommendEmptyAlcoholViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,RecommendAlcoholItemBinding>(R.layout.recommend_alcohol_empty_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.recommendItem = data
        binding.executePendingBindings()

    }
}