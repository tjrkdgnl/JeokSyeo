package com.application

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.model.user.UserInfo
import com.sharedpreference.UserDB
import com.vuforia.engine.wet.R
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.util.*

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        userBuilder = UserInfo.Builder("")
        KakaoSdk.init(this, getString(R.string.kakaoNativeKey))
        userInfo = UserInfo()
        userDataBase = UserDB.getInstance(this)
        RxjavaErrorHandling()
    }

    companion object {
        var instance: Application = Application()
            private set

        lateinit var userInfo: UserInfo
        lateinit var userBuilder: UserInfo.Builder

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

    private fun RxjavaErrorHandling() {
        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            if (e is UndeliverableException) {
                e.cause
            }
            if (e is IOException) {
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                Objects.requireNonNull(
                    Thread.currentThread().uncaughtExceptionHandler
                ).uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                Objects.requireNonNull(
                    Thread.currentThread().uncaughtExceptionHandler
                ).uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e != null) {
                Log.e(
                    "RxJava_HOOK",
                    "Undeliverable exception received, not sure what to do" + e.message
                )
            }
        }
    }

}