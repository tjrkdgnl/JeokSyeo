package com.jeoksyeo.wet.activity.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.base.DefaultPresenter
import com.vuforia.engine.wet.databinding.MainBinding

interface MainContract {

    interface BaseView{
        fun getView() :MainBinding

        fun refresh()
    }

    interface BasePresenter : DefaultPresenter{
        var view:BaseView

        fun initCarouselViewPager(context:Context)

        fun initRecommendViewPager(context: Context)

        fun initAlcholRanking(context: Context)
    }
}