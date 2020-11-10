package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alcohol_detail.AlcoholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemSrmBinding

class Alcohol_Component_SRM(parent:ViewGroup)
    :BaseViewHolder<AlcoholComponentData,AlcoholComponentItemSrmBinding>(R.layout.alcohol_component_item_srm,parent) {

    override fun bind(data: AlcoholComponentData) {
        binding.componentSRM = data
        binding.executePendingBindings()

    }
}