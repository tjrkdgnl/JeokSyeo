package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ViewpagerGridItemBinding

class AlcoholCategoryGridViewHolder(parent:ViewGroup): BaseViewHolder<AlcoholList,ViewpagerGridItemBinding>(R.layout.viewpager_grid_item,parent) {
    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()


        data.likeCount?.let {
            binding.textViewGridHeartCount.text = GlobalApplication.instance.checkCount(it)
        }

        data.review?.reviewCount?.let {
            binding.textViewGridCommentCount.text =GlobalApplication.instance.checkCount(it)
        }

        data.viewCount?.let {
            binding.categoryGridEyeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }
}