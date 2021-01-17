package com.jeoksyeo.wet.activity.favorite

import android.content.Context
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

interface FavoriteContract {

    interface BaseView{
        fun getBinding():FavoriteActivityBinding

        fun setHeaderInit()
    }

    interface BasePresenter{
        var view:BaseView
        var context:Context
        fun detach()
        fun initTabLayout()
        fun initProfile()
        fun setNetworkUtil()
    }
}