package com.adapter.viewholder

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.navigation.NavigationItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NavigationItemBinding

class NavigationViewHolder(val parent: ViewGroup) :
    BaseViewHolder<NavigationItem, NavigationItemBinding>(R.layout.navigation_item, parent) {

    @SuppressLint("SetTextI18n")
    override fun bind(data: NavigationItem) {
        binding.navigationItem = data
        binding.executePendingBindings()
        
        if (data.boldCheck) {
            binding.navigationTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(20f))

            if (data.title == "테이스트 저널" || data.title == "Contact Us") {
                binding.rightErrow.visibility = View.INVISIBLE
            }

            val typeface =
                Typeface.createFromAsset(parent.context.assets, "apple_sd_gothic_neo_sb.ttf")
            binding.navigationTitle.typeface = typeface
            binding.navigationTitle.setTypeface(null, Typeface.BOLD)
        }
        else{
            binding.navigationTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))
        }
    }
}