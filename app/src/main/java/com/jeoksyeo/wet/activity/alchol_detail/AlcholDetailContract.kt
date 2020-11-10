package com.jeoksyeo.wet.activity.alchol_detail

import android.content.Context
import android.content.Intent
import com.model.alchol_detail.Alchol
import com.vuforia.engine.wet.databinding.AlcholDetailBinding

interface AlcholDetailContract {

    interface  BaseView{
        fun getView():AlcholDetailBinding

        fun setLikeImage(isLike:Boolean)

    }

    interface BasePresenter{
        var view:BaseView
        var context:Context
        var intent: Intent

        fun init()

        fun executeLike()

        fun cancelAlcholLike()

        fun initComponent(context: Context)

        fun initReview(context: Context)

        fun expandableText()

        fun checkReviewDuplicate(context: Context)
    }
}