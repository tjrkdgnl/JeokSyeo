package com.activities.setting

import android.app.Activity
import android.content.Context
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.SettingBinding

interface SettingContract {

    interface SettingView:BaseView<SettingBinding>{

        fun setStatusBarInit()
    }

    interface SettingPresenter:BasePresenter<SettingBinding>{
        var view:SettingView

        fun initItem(context: Context,activity:Activity)

        fun detachView()
    }

}