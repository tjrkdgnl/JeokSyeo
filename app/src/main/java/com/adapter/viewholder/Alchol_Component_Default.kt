package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_detail.AlcholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholComponentItemDefaultBinding
import com.vuforia.engine.wet.databinding.AlcholComponentItemRecyclerviewBinding

class Alchol_Component_Default(parent:ViewGroup)
    : BaseViewHolder<AlcholComponentData,AlcholComponentItemDefaultBinding>(R.layout.alchol_component_item_default,parent) {

    override fun bind(data: AlcholComponentData) {
        binding.componentDefaultItem = data
        binding.executePendingBindings()

        data.compo_image?.let {
            binding.componentBackground.setImageResource(it)
        }
    }
}