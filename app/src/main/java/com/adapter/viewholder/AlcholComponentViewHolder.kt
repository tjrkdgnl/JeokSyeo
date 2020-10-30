package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_detail.AlcholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholComponentItemBinding

class AlcholComponentViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcholComponentData,AlcholComponentItemBinding>(R.layout.alchol_component_item,parent) {

    override fun bind(data: AlcholComponentData) {
        binding.component = data
        binding.executePendingBindings()
    }
}