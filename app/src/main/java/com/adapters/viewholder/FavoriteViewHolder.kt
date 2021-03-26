package com.adapters.viewholder

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.favorite.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteItemBinding

/**
 * 내가 찜한 주류화면에서 보여질 뷰홀더
 */
class FavoriteViewHolder( parent: ViewGroup) :
    BaseViewHolder<AlcoholList, FavoriteItemBinding>(R.layout.favorite_item, parent) {


    @SuppressLint("SetTextI18n")
    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

        //알코올 도수 값 설정
        data.abv?.let {
            binding.favoriteAbv.text = "${String.format("%.1f",it)}%"

        }
    }
}