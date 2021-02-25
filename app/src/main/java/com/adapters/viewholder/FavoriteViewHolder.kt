package com.adapters.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.favorite.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteItemBinding

class FavoriteViewHolder( parent: ViewGroup) :
    BaseViewHolder<AlcoholList, FavoriteItemBinding>(R.layout.favorite_item, parent) {


    @SuppressLint("SetTextI18n")
    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

        data.abv?.let {
            binding.favoriteAbv.text = "${String.format("%.1f",it)}%"

        }


    }
}