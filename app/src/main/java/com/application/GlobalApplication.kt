package com.application

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.model.UserInfo
import com.sharedpreference.UserDB
import com.vuforia.engine.wet.R

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        userBuilder = UserInfo.Builder("")
        KakaoSdk.init(this, getString(R.string.kakaoNativeKey))
        userInfo = UserInfo()
        userDataBase = UserDB.getInstance(this)
    }

    companion object {
        var instance: Application = Application()
            private set

        lateinit var userInfo: UserInfo
        lateinit var userBuilder:UserInfo.Builder

        lateinit var userDataBase: UserDB

        const val NICKNAME = "nickname"
        const val BIRTHDAY = "birth"
        const val GENDER = "gender"
        const val CONGRATULATION = "congratulation"
        const val LOCATION = "location"
        const val USER_ID ="user_id"
        const val OAUTH_PROVIDER ="oauth_provider"
        const val OAUTH_TOKEN ="oauth_token"
        const val REFRESH_TOKEN="refresh_token"
        const val ADDRESS ="address"
    }

}