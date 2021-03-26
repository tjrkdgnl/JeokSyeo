package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ComponentDepthItemBinding

/**
 * 컴포넌트가 이중 리싸이클러뷰를 갖는 경우에 child recyclerview에서 사용될 뷰홀더
 */
class DepthComponentViewholder(parent:ViewGroup) :BaseViewHolder<String,ComponentDepthItemBinding>(R.layout.component_depth_item,parent) {

    override fun bind(data: String) {
        binding.component =data
        binding.executePendingBindings()
    }
}