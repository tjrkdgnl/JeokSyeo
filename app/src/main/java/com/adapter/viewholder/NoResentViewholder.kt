package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.fragment.search.SearchContract
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoResentSearchItemBinding

class NoResentViewholder(parent:ViewGroup,  private val searchInterface: SearchContract.BaseVIew) : BaseViewHolder<String,NoResentSearchItemBinding>(R.layout.no_resent_search_item,parent) {
    override fun bind(data: String) {


    }
}