package com.jeoksyeo.wet.activity.alcohol_detail

import android.content.Context
import android.content.Intent
import com.model.alcohol_detail.Alcohol
import com.model.alcohol_detail.Review
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding

interface AlcoholDetailContract {

    interface  BaseView{
        fun getView():AlcoholDetailBinding

        fun setLikeImage(isLike:Boolean)

    }

    interface BasePresenter{
        var view:BaseView
        var context:Context
        var intent: Intent

        fun init()

        fun executeLike()

        fun cancelAlcoholLike()

        fun initComponent(context: Context)

        fun initReview(context: Context)

        fun expandableText()

        fun checkReviewDuplicate(context: Context)

        fun initRadarChart(review:Review)


    }
}