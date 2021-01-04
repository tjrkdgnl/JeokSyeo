package com.jeoksyeo.wet.activity.main

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.vuforia.engine.wet.databinding.MainBinding

interface MainContract {

    interface BaseView{
        fun getView() : MainBinding

    }

    interface BasePresenter {
        var view:BaseView
        var activity:Activity

        fun initBanner(context:Context)

        fun initRecommendViewPager(context: Context)

        fun initAlcoholRanking(context: Context)

        fun detachView()

        fun checkLogin(context: Context)

        fun setNetworkUtil()


        fun handleDeepLink()

        fun createDynamicLink()

        fun checkDeepLink(): Uri

        fun getAlcohol(alcoholId: String)
    }
}