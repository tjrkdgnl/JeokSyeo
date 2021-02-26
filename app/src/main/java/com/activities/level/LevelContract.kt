package com.activities.level

import android.content.Context
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.LevelBinding

interface LevelContract {

    interface LevelView: BaseView<LevelBinding> {
        fun settingMainAlcholGIF(level:Int)

        fun settingExperience(reviewCount:Int,level: Int)

        fun finalLevel()

        fun setHeaderInit()

    }

    interface LevelPresenter:BasePresenter<LevelBinding>{
        var view:LevelView

        fun getMyLevel()

        fun initMiniImageArray()

        fun detach()
    }
}