package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchItemRelativeBinding

class NoRelativeSearchViewHolder(parent: ViewGroup) :
    BaseViewHolder<String, SearchItemRelativeBinding>(R.layout.search_item_no_relative, parent) {

    override fun bind(data: String) {

    }
}