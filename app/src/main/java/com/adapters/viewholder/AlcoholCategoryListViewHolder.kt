package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ViewpagerListItemBinding

/**
 * 주류 카테고리 화면에서 list로 보여질 경우, 각 아이템 뷰홀더
 */
class AlcoholCategoryListViewHolder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,ViewpagerListItemBinding>(R.layout.viewpager_list_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

        //각 아이템 마다 좋아요한 개수 셋팅
        data.likeCount?.let {
            binding.textViewListHeartCount.text = GlobalApplication.instance.checkCount(it)
        }

        //각 아이템 마다 리뷰 개수 셋팅
        data.review?.reviewCount?.let {
            binding.textViewListCommentCount.text = GlobalApplication.instance.checkCount(it)
        }

        //각 아이템 마다 조회수 개수 셋팅
        data.viewCount?.let {
            binding.listEyeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }
}