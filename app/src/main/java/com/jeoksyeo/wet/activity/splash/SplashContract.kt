package com.jeoksyeo.wet.activity.splash

import android.app.Activity
import com.vuforia.engine.wet.databinding.SplashBinding

interface SplashContract {

    interface BaseView {
        fun getBinding(): SplashBinding
    }

    interface BasePresenter {
        var view: BaseView
        var activity: Activity

        fun setUserInfo()
        fun moveActivity()
        fun detach()
        suspend fun versionCheck(): Boolean

    }

}