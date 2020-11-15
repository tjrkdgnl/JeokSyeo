package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.favorite.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteItemBinding

class FavoriteViewHolder(parent:ViewGroup):BaseViewHolder<AlcoholList,FavoriteItemBinding>(R.layout.favorite_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()
    }
}