package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ComponentDepthItemBinding

class DepthComponentViewholder(parent:ViewGroup) :BaseViewHolder<String,ComponentDepthItemBinding>(R.layout.component_depth_item,parent) {

    override fun bind(data: String) {
        binding.component =data
        binding.executePendingBindings()
    }
}