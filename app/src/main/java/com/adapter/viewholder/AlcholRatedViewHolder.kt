package com.adapter.viewholder

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.custom.CustomDialog
import com.jeoksyeo.wet.activity.login.Login
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