package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.rated.ReviewList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholRatedItemBinding

class AlcholRatedViewHolder(val parent:ViewGroup):BaseViewHolder<ReviewList,AlcholRatedItemBinding>(R.layout.alchol_rated_item,parent) {

    override fun bind(data: ReviewList) {
        getViewBinding().review = data
        getViewBinding().executePendingBindings()

        data.score?.let {
            getViewBinding().ratedItemRatingbar.rating = it.toFloat()
        }

    }
}