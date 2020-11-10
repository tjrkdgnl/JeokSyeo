package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.rated.ReviewList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRatedItemBinding

class AlcoholRatedViewHolder(val parent:ViewGroup):BaseViewHolder<ReviewList,AlcoholRatedItemBinding>(R.layout.alcohol_rated_item,parent) {

    override fun bind(data: ReviewList) {
        getViewBinding().review = data
        getViewBinding().executePendingBindings()

        data.score?.let {
            getViewBinding().ratedItemRatingbar.rating = it.toFloat()
        }

    }
}