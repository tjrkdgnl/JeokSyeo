package com.jeoksyeo.wet.activity.level

import android.content.Context
import com.vuforia.engine.wet.databinding.LevelBinding

interface LevelContract {

    interface BaseView{
        fun getView():LevelBinding

        fun settingMainAlcholGIF(level:Int)

        fun settingExperience(reviewCount:Int,level: Int)

        fun finalLevel()

        fun setHeaderInit()

    }

    interface BasePresenter{
        var view:BaseView
        var context:Context

        fun getMyLevel()

        fun initMiniImageArray()

        fun detach()

        fun setNetworkUtil()

    }
}