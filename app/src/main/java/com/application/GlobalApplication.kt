package com.application

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.vuforia.engine.wet.R

class GlobalApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        instance =this
        userInfo = com.model.UserInfo()
        KakaoSdk.init(this,getString(R.string.kakaoNativeKey))
    }


    companion object{
         var instance : Application = Application()
         private set

        const val NICKNAME = "nickname"
        const val BIRTHDAY = "birth"
        const val GENDER = "gender"
        const val CONGRATULATION ="congratulation"
        const val LOCATION ="location"

        lateinit var userInfo: com.model.UserInfo
    }

}