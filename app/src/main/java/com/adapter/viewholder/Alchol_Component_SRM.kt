package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_detail.AlcholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholComponentItemSrmBinding

class Alchol_Component_SRM(parent:ViewGroup)
    :BaseViewHolder<AlcholComponentData,AlcholComponentItemSrmBinding>(R.layout.alchol_component_item_srm,parent) {

    override fun bind(data: AlcholComponentData) {
        binding.componentSRM = data
        binding.executePendingBindings()

    }
}