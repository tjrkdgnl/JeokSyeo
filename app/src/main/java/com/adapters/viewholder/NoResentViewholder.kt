package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.fragments.search.SearchContract
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoResentSearchItemBinding

/**
 * 최근 검색어가 없는 경우 핸들링 되는 뷰홀더
 */
class NoResentViewholder(parent:ViewGroup) : BaseViewHolder<String,NoResentSearchItemBinding>(R.layout.no_resent_search_item,parent) {
    override fun bind(data: String) {


    }
}