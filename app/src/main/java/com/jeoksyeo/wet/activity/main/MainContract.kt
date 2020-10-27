package com.jeoksyeo.wet.activity.main

import android.app.Activity
import android.content.Context
import com.vuforia.engine.wet.databinding.MainBinding

interface MainContract {

    interface BaseView{
        fun getView() :MainBinding

        fun refresh()
    }

    interface BasePresenter{
        var view:BaseView

        fun initCarouselViewPager(context:Context)

        fun initRecommendViewPager(context: Context)

        fun initNavigationItemSet(context: Context, activity: Activity, provider:String?)

        fun initAlcholRanking(context: Context)

        fun detachView()

        fun checkLogin(context:Context,provider:String?)
    }
}