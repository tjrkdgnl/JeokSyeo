package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_category.AlcholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ViewpagerGridItemBinding

class AlcholCategoryGridViewHolder(parent:ViewGroup): BaseViewHolder<AlcholList,ViewpagerGridItemBinding>(R.layout.viewpager_grid_item,parent) {
    override fun bind(data: AlcholList) {
        binding.alchol = data
        binding.executePendingBindings()
    }

}