package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoRatedItemBinding

class AlcholNoRatedViewHolder(parent:ViewGroup) : BaseViewHolder<String,NoRatedItemBinding>(R.layout.no_rated_item,parent) {

    override fun bind(data: String) {
    }
}