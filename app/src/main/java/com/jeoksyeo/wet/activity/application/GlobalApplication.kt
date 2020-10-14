package com.jeoksyeo.wet.activity.application

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.vuforia.engine.wet.R

class GlobalApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        instance =this
        KakaoSdk.init(this,getString(R.string.kakaoNativeKey))
    }


    companion object{
        lateinit var instance : Application
         private set
    }

}