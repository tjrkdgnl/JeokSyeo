package com.jeoksyeo.wet.activity.application

import android.app.Application
import com.google.firebase.auth.UserInfo
import com.kakao.sdk.common.KakaoSdk
import com.vuforia.engine.wet.R

class GlobalApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        instance =this
        userInfo =model.UserInfo()
        KakaoSdk.init(this,getString(R.string.kakaoNativeKey))
    }


    companion object{
         var instance : Application = Application()
         private set

        lateinit var userInfo:model.UserInfo
    }

}