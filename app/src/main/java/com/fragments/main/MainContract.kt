package com.fragments.main

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.vuforia.engine.wet.databinding.MainBinding

interface MainContract {

    interface BaseView{
        fun getViewBinding() : MainBinding

    }

    interface BasePresenter {
        var view: BaseView
        var activity:FragmentActivity

        fun initBanner(context:Context)

        fun initRecommendViewPager(context: Context)

        fun initAlcoholRanking(context: Context)

        fun detachView()

    }
}