package com.adapter.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alcohol_detail.AlcoholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemDefaultBinding
import com.vuforia.engine.wet.databinding.AlcoholComponentItemRecyclerviewBinding

class Alcohol_Component_Default(parent:ViewGroup)
    : BaseViewHolder<AlcoholComponentData,AlcoholComponentItemDefaultBinding>(R.layout.alcohol_component_item_default,parent) {

    @SuppressLint("SetTextI18n")
    override fun bind(data: AlcoholComponentData) {
        binding.componentDefaultItem = data
        binding.executePendingBindings()

        data.textSize?.let {
            binding.componentDescription.textSize = it
        }

        data.compo_image?.let {
            binding.componentBackground.setImageResource(it)
        }

        if(data.contents?.get(0).equals("TRUE"))
            binding.componentDescription.text ="YES"
        else if(data.contents?.get(0).equals("FALSE"))
            binding.componentDescription.text ="NO"
    }
}