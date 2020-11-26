package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alcohol_ranking.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRankItemBinding

class AlcoholRankViewHolder(parent:ViewGroup) :BaseViewHolder<AlcoholList,AlcoholRankItemBinding>(R.layout.alcohol_rank_item,parent) {
    override fun bind(data: AlcoholList) {
        binding.alcoholRank = data
        binding.executePendingBindings()


        data.review?.reviews?.let { lst->
            if(lst.isNotEmpty()){
                lst[0].contents?.let { comment->
                    getViewBinding().monthlyBestComment.text = comment
                }
            }
        }

    }
}