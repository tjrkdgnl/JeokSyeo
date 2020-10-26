package com.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.custom.BitMapTransformation

class BindingAdapter {
    
    companion object{
        @JvmStatic
        @BindingAdapter("setting_icon_Res")
        fun navigation_icon(imageView: ImageView, ResId:Int){
            imageView.setImageResource(ResId)
        }

        @JvmStatic
        @BindingAdapter("setting_icon_str")
        fun setting_string(imageView: ImageView, ResStr:String){
            Glide.with(imageView.context)
                .load(ResStr)

                .into(imageView)
        }
    }
    
}