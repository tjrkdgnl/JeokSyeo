package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoReviewItemBinding

/**
 * 내가 평가한 주류화면에서 해당 타입의 주류를 평가한 적이 없을때 핸들링 되는 뷰홀더
 */
class NoAlcoholReviewViewHolder(parent:ViewGroup):BaseViewHolder<String,NoReviewItemBinding>(R.layout.no_review_item,parent) {
    override fun bind(data: String) {
    }
}