package com.jeoksyeo.wet.activity.setting

import android.app.Activity
import android.content.Context
import com.vuforia.engine.wet.databinding.SettingBinding

interface SettingContract {

    interface BaseView{
        fun getView():SettingBinding

        fun setStatusBarInit()
    }

    interface BasePresenter{
        var view:BaseView

        fun initItem(context: Context,activity:Activity)

        fun detachView()
    }

}