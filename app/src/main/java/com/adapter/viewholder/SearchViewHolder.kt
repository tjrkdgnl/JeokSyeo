package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.jeoksyeo.wet.activity.search.SearchContract
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchItemRelativeBinding

class SearchViewHolder(parent: ViewGroup, val searchInterface: SearchContract.BaseVIew) :
    BaseViewHolder<String, SearchItemRelativeBinding>(R.layout.search_item_relative, parent) {

    override fun bind(data: String) {
        getViewBinding().item = data
        getViewBinding().executePendingBindings()


        getViewBinding().searchContentsLayout.setOnClickListener {
            searchInterface.changeAdapter(data)
        }
    }
}