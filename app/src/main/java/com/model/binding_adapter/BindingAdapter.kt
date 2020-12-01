package com.model.binding_adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

class BindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter("setting_icon_Res")
        fun navigation_icon(imageView: ImageView, ResId: Int) {
            imageView.setImageResource(ResId)
        }

        @JvmStatic
        @BindingAdapter("setting_icon_str")
        fun setting_string(imageView: ImageView, ResStr: String) {
            Glide.with(imageView.context)
                .load(ResStr)
                .apply(RequestOptions()
                        .signature(ObjectKey("signature true"))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(imageView)
        }
    }
}