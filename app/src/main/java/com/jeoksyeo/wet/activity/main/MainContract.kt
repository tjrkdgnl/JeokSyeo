package com.jeoksyeo.wet.activity.main

import android.content.Context
import com.vuforia.engine.wet.databinding.MainBinding

interface MainContract {

    interface BaseView{
        fun getView() :MainBinding
    }

    interface BasePresenter{
        var view:BaseView

        fun initCarouselViewPager(context:Context)

        fun initRecommendViewPager(context: Context)

        fun initNavigationItemSet(context: Context)

        fun initAlcholRanking(context: Context)

        fun detachView()
    }
}