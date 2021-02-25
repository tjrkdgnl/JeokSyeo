package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ViewpagerListItemBinding

class AlcoholCategoryListViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,ViewpagerListItemBinding>(R.layout.viewpager_list_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()


        data.likeCount?.let {
            binding.textViewListHeartCount.text = GlobalApplication.instance.checkCount(it)
        }

        data.review?.reviewCount?.let {
            binding.textViewListCommentCount.text = GlobalApplication.instance.checkCount(it)
        }

        data.viewCount?.let {
            binding.listEyeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }
}