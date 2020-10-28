package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_category.AlcholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ViewpagerListItemBinding

class AlcholCategoryListViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcholList,ViewpagerListItemBinding>(R.layout.viewpager_list_item,parent) {

    override fun bind(data: AlcholList) {
        binding.alchol = data
        binding.executePendingBindings()
    }
}