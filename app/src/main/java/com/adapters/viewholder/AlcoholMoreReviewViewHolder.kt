package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MoreReviewItemBinding

/**
 * 주류 리뷰 더 보기 뷰를 표시하기 위한 뷰홀더

 */
class AlcoholMoreReviewViewHolder(parent: ViewGroup)
    : BaseViewHolder<String, MoreReviewItemBinding>(R.layout.more_review_item, parent) {

    override fun bind(data: String) {
    }
}