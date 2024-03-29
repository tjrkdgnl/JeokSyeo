package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoRatedItemBinding

/**
 * 평가한 주류가 없을때 보여질 뷰홀더
 */
class AlcoholNoRatedViewHolder(parent:ViewGroup) : BaseViewHolder<String,NoRatedItemBinding>(R.layout.no_rated_item,parent) {

    override fun bind(data: String) {
    }
}