package com.model.navigation

import android.widget.ImageView
import androidx.databinding.BindingAdapter

data class NavigationItem(
     var icon:Int,
     var title:String) {


    companion object{
        ////이처럼 특수하게 static으로 인식해야 하는 상황에 사용해야한다.
        @JvmStatic
        @BindingAdapter("setting_icon")
        fun setting_icon(imageView:ImageView,ResId:Int){
            imageView.setImageResource(ResId)
        }
    }
}