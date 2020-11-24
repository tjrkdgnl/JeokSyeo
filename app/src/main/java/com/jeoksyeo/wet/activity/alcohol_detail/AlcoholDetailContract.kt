package com.jeoksyeo.wet.activity.alcohol_detail

import android.content.Context
import android.content.Intent
import com.model.alcohol_detail.Alcohol
import com.model.review.EvaluateIndicator
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding

interface AlcoholDetailContract {

    interface  BaseView{
        fun getView():AlcoholDetailBinding

        fun setLikeImage(isLike:Boolean)

        fun settingExpandableText(check :Boolean)

        fun settingProgressBar(check:Boolean)
    }

    interface BasePresenter{
        var view:BaseView
        var context:Context
        var intent: Intent
        var alcohol:Alcohol

        fun init()

        fun executeLike()

        fun initComponent(context: Context)

        fun initReview(context: Context)

        fun expandableText()

        fun checkReviewDuplicate(context: Context)

        fun initRadarChart(review:EvaluateIndicator)

        fun checkCountLine()

        fun refreshIsLike()

    }
}