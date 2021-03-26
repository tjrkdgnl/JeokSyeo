package com.adapters.viewholder

import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchListItemBinding

/**
 *  유저가 검색버튼을 클릭했을 때, 표시될 리스트 아이템 뷰홀더
 */
class SearchListViewholder(parent:ViewGroup)
    : BaseViewHolder<AlcoholList,SearchListItemBinding>(R.layout.search_list_item,parent) {

    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

        //유저들이 찜한 개수 셋팅
        data.likeCount?.let {
            binding.textViewListHeartCount.text = GlobalApplication.instance.checkCount(it)
        }
        //해당 주류의 총 리뷰 개수 셋팅
        data.review?.reviewCount?.let {
            binding.textViewListCommentCount.text = GlobalApplication.instance.checkCount(it)
        }

        //해당 주류 조회수 셋팅
        data.viewCount?.let {
            binding.listEyeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }
}