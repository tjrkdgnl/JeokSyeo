package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.alchol_ranking.AlcholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholRankItemBinding

class AlcholRankViewHolder(parent:ViewGroup) :BaseViewHolder<AlcholList,AlcholRankItemBinding>(R.layout.alchol_rank_item,parent) {
    override fun bind(data: AlcholList) {
        binding.alcholRank = data
        binding.executePendingBindings()
    }
}