package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alcohol_ranking.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRankItemBinding

//이달의 주류를 보여주기 위한 뷰홀더
class AlcoholRankViewHolder(parent:ViewGroup) :BaseViewHolder<AlcoholList,AlcoholRankItemBinding>(R.layout.alcohol_rank_item,parent) {
    override fun bind(data: AlcoholList) {
        binding.alcoholRank = data
        binding.executePendingBindings()

        //해당 주류에서 가장 좋아요가 많았던 리뷰를 셋팅
        data.review?.reviews?.let { lst->
            if(lst.isNotEmpty()){
                lst[0].contents?.let { comment->
                    getViewBinding().monthlyBestComment.text = comment
                }
            }
        }
    }
}