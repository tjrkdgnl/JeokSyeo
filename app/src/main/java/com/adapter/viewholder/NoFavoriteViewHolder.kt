package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoReviewItemBinding

class NoFavoriteViewHolder(parent:ViewGroup):BaseViewHolder<String,NoReviewItemBinding>(R.layout.no_favorite_item,parent) {
    override fun bind(data: String) {
    }
}