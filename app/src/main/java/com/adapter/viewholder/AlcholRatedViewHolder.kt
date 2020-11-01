package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholRatedItemBinding

class AlcholRatedViewHolder(parent:ViewGroup):BaseViewHolder<String,AlcholRatedItemBinding>(R.layout.alchol_rated_item,parent) {

    override fun bind(data: String) {
        getViewBinding().name = data
        getViewBinding().executePendingBindings()
    }
}