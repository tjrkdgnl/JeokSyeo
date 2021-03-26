package com.adapters.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alcohol_detail.AlcoholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemDefaultBinding

class AlcoholComponentDefault(parent: ViewGroup) :
    BaseViewHolder<AlcoholComponentData, AlcoholComponentItemDefaultBinding>(
        R.layout.alcohol_component_item_default,
        parent
    ) {

    @SuppressLint("SetTextI18n")
    override fun bind(data: AlcoholComponentData) {
        binding.componentDefaultItem = data
        binding.executePendingBindings()

        //컴포넌트에 표시될 텍스트 사이즈 설정
        data.textSize?.let {
            binding.componentDescription.textSize = it
        }

        //컴포넌트에 표시될 컴포넌트 이미지 설정
        data.compo_image?.let {
            binding.componentBackground.setImageResource(it)
        }

        //컴포넌트에 표시될 값 설정
        if (data.contents is Float || data.contents is Int) {
            binding.componentDescription.text = data.contents.toString()
        }
        else if (data.contents is String) {
            when (data.contents) {
                "true" -> binding.componentDescription.text = "YES"
                "false" -> binding.componentDescription.text = "NO"
                else -> {
                    binding.componentDescription.text = data.contents
                }
            }
        }
    }
}