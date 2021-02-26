package com.activities.splash

import android.app.Activity
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.SplashBinding

interface SplashContract {

    interface SplashView:BaseView<SplashBinding>

    interface SplashPresenter :BasePresenter<SplashBinding> {
        var view: SplashView

        suspend fun setUserInfo():Boolean
        fun moveActivity()
        fun detach()
        suspend fun versionCheck(): Boolean
    }

}