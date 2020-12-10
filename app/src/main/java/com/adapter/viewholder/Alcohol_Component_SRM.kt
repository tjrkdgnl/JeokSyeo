package com.adapter.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.base.BaseViewHolder
import com.model.alcohol_detail.AlcoholComponentData
import com.model.alcohol_detail.Srm
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemSrmBinding

class Alcohol_Component_SRM(val parent: ViewGroup) :
    BaseViewHolder<AlcoholComponentData, AlcoholComponentItemSrmBinding>(
        R.layout.alcohol_component_item_srm,
        parent
    ) {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun bind(data: AlcoholComponentData) {
        if (data.contents is Srm) {
            binding.componentParentLayout.setBackgroundColor(Color.parseColor(data.contents.rgbHex))
            binding.componentTitle.text = "SRM"
            binding.componentSrm.text = data.contents.srm.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.componentTitle.setTextColor(parent.context.resources.getColor(R.color.white, null))

                binding.componentSrm.setTextColor(
                    parent.context.resources.getColor(R.color.white, null)
                )
                binding.componentBorder.setBackgroundColor(
                    parent.context.resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.componentSrm.setTextColor(parent.context.resources.getColor(R.color.white, null))
                binding.componentBorder.setBackgroundColor(parent.context.resources.getColor(R.color.white, null))

            }
            else{
                binding.componentTitle.setTextColor(ContextCompat.getColor(parent.context,R.color.white))

                binding.componentSrm.setTextColor(
                    ContextCompat.getColor(parent.context,R.color.white))

                binding.componentBorder.setBackgroundColor(
                    ContextCompat.getColor(parent.context,R.color.white))

                binding.componentSrm.setTextColor(ContextCompat.getColor(parent.context,R.color.white))
                binding.componentBorder.setBackgroundColor(ContextCompat.getColor(parent.context,R.color.white))

            }

            val drawable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    parent.context.resources.getDrawable(R.drawable.alcohol_component_srm, null)
                } else {
                  ContextCompat.getDrawable(parent.context,R.drawable.alcohol_component_srm)
                }
            val wrapDrawable = DrawableCompat.wrap(drawable!!)

            DrawableCompat.setTint(wrapDrawable, Color.parseColor(data.contents.rgbHex))
            binding.componentParentLayout.background = drawable

        } else if (data.contents is com.model.alcohol_detail.Color) {
            binding.componentTitle.text = "COLOR"
            binding.componentSrm.text = data.contents.name

            if (data.contents.rgbHex != "#FFFFFF") {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.componentTitle.setTextColor(parent.context.getColor(R.color.white))
                    binding.componentBorder.setBackgroundColor(parent.context.getColor(R.color.white))
                    binding.componentSrm.setTextColor(parent.context.getColor(R.color.white))
                    binding.compoentTitleKr.setTextColor(parent.context.getColor(R.color.white))
                }else{
                    binding.componentTitle.setTextColor(ContextCompat.getColor(parent.context,R.color.white))
                    binding.componentBorder.setBackgroundColor(ContextCompat.getColor(parent.context,R.color.white))
                    binding.componentSrm.setTextColor(ContextCompat.getColor(parent.context,R.color.white))
                    binding.compoentTitleKr.setTextColor(ContextCompat.getColor(parent.context,R.color.white))
                }

                val drawable =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        parent.context.resources.getDrawable(R.drawable.alcohol_component_srm, null)
                    } else {
                        ContextCompat.getDrawable(parent.context,R.drawable.alcohol_component_srm)
                    }

                val wrapDrawable = DrawableCompat.wrap(drawable!!)
                DrawableCompat.setTint(wrapDrawable, Color.parseColor(data.contents.rgbHex))
                binding.componentParentLayout.background = drawable

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.componentTitle.setTextColor(parent.context.getColor(R.color.black))
                    binding.componentBorder.setBackgroundColor(parent.context.getColor(R.color.black))
                    binding.componentSrm.setTextColor(parent.context.getColor(R.color.black))
                    binding.compoentTitleKr.setTextColor(parent.context.getColor(R.color.black))
                }else{
                    binding.componentTitle.setTextColor(ContextCompat.getColor(parent.context,R.color.black))
                    binding.componentBorder.setBackgroundColor(ContextCompat.getColor(parent.context,R.color.black))
                    binding.componentSrm.setTextColor(ContextCompat.getColor(parent.context,R.color.black))
                    binding.compoentTitleKr.setTextColor(ContextCompat.getColor(parent.context,R.color.black))
                }
            }
        }
    }
}

