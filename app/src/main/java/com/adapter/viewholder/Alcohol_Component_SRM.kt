package com.adapter.viewholder

import android.graphics.Color
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alcohol_detail.AlcoholComponentData
import com.model.alcohol_detail.Srm
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemSrmBinding

class Alcohol_Component_SRM(val parent:ViewGroup)
    :BaseViewHolder<AlcoholComponentData,AlcoholComponentItemSrmBinding>(R.layout.alcohol_component_item_srm,parent) {

    override fun bind(data: AlcoholComponentData) {
        if(data.contents is Srm){
                binding.componentParentLayout.setBackgroundColor(Color.parseColor(data.contents.rgbHex))
                binding.componentTitle.text = "COLOR"
                binding.componentSrm.text = data.contents.color
                binding.componentParentLayout.setBackgroundColor( parent.context.resources.getColor(R.color.white,null))
                binding.componentTitle.setTextColor(parent.context.resources.getColor(R.color.orange,null))
                binding.componentSrm.setTextColor(parent.context.resources.getColor(R.color.black,null))
                binding.componentBorder.setBackgroundColor(parent.context.resources.getColor(R.color.orange,null))

        }
        else if(data.contents is com.model.alcohol_detail.Color){
            binding.componentTitle.text = "COLOR"
            binding.componentSrm.text = data.contents.name
            binding.componentTitle.setTextColor(parent.context.getColor(R.color.black))
            binding.componentBorder.setBackgroundColor(parent.context.getColor(R.color.black))
            binding.componentSrm.setTextColor(parent.context.getColor(R.color.black))
            binding.componentParentLayout.setBackgroundColor(Color.parseColor(data.contents.rgbHex))
        }
    }
}