package com.adapter.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_detail.AlcholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholComponentItemDefaultBinding
import com.vuforia.engine.wet.databinding.AlcholComponentItemRecyclerviewBinding

class Alchol_Component_Default(parent:ViewGroup)
    : BaseViewHolder<AlcholComponentData,AlcholComponentItemDefaultBinding>(R.layout.alchol_component_item_default,parent) {

    @SuppressLint("SetTextI18n")
    override fun bind(data: AlcholComponentData) {
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