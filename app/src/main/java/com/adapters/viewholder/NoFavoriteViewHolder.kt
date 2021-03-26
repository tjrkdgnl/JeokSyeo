package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoReviewItemBinding

/**
 * 내가 찜한 주류에서 해당 타입의 주류를 찜한 적이 없을 대 표시되는 뷰홀더
 */
class NoFavoriteViewHolder(parent:ViewGroup):BaseViewHolder<String,NoReviewItemBinding>(R.layout.no_favorite_item,parent) {
    override fun bind(data: String) {
    }
}