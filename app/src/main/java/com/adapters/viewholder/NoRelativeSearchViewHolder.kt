package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchItemRelativeBinding

/**
 * 유저가 입력하고 있는 키워드에 대한 연관검색어가 없을때 표시되어 질 뷰홀더
 */
class NoRelativeSearchViewHolder(parent: ViewGroup) :
    BaseViewHolder<String, SearchItemRelativeBinding>(R.layout.search_item_no_relative, parent) {

    override fun bind(data: String) {

    }
}