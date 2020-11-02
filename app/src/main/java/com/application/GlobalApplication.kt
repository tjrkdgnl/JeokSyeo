package com.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    private val typeList = listOf<String>("TR", "BE", "WI", "SA", "FO")

    override fun onCreate() {
        super.onCreate()
        userBuilder = UserInfo.Builder("")
        KakaoSdk.init(this, getString(R.string.kakaoNativeKey))
        userInfo = UserInfo()
        userDataBase = UserDB.getInstance(this)
        RxjavaErrorHandling()
    }

    companion object {
        //싱글턴 객체 생성
        var instance = GlobalApplication()
            private set

        //유저 정보
        lateinit var userInfo: UserInfo
        lateinit var userBuilder: UserInfo.Builder
        lateinit var userDataBase: UserDB

        //디바이스 사이즈
        var device_width = 0
        var device_height = 0

        const val NICKNAME = "nickname"
        const val BIRTHDAY = "birth"
        const val GENDER = "gender"
        const val LOCATION = "location"
        const val USER_ID = "user_id"
        const val OAUTH_PROVIDER = "oauth_provider"
        const val OAUTH_TOKEN = "oauth_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val ADDRESS = "address"
        const val ACTIVITY_HANDLING ="activity_handling"
        const val USER_BUNDLE = "userBundle"
        const val ACTIVITY_HANDLING_BUNDLE ="activityHandling_bundle"

        const val CATEGORY_BUNDLE ="category_bundle"
        const val ALCHOL_BUNDLE = "alchol"
        const val CATEGORY_SIZE = 5
        const val MOVE_TYPE = "type"
        const val PAGINATION_SIZE= 20
        const val DEFAULT_SORT = "like"
        const val MOVE_ALCHOL = "alchol Data"
        const val ALCHOL_LIKE = "likeAndDisLike"

        const val ACTIVITY_HANDLING_MAIN = 0
        const val ACTIVITY_HANDLING_DETAIL = 1
        const val ACTIVITY_HANDLING_COMMENT = 3
        const val ACTIVITY_HANDLING_CATEGORY = 2
    }

    fun getAlcholType(positon: Int): String {
        return typeList[positon]
    }


    //액티비티 전환
    fun moveActivity(context: Context,activityClass:Class<*>,flag:Int=0,bundle:Bundle? = null,bundleFlag:String?=null){
        var activity = context as Activity
        var intent = Intent(context,activityClass)

        bundle?.let {bun->
            bundleFlag?.let {flag->
                intent.putExtra(flag,bun)
            }
        }

        when(flag){
            0 ->{  activity.startActivity(intent)}

            Intent.FLAG_ACTIVITY_SINGLE_TOP ->{
                activity.startActivity(intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP )) }

            Intent.FLAG_ACTIVITY_CLEAR_TOP ->{
                activity.startActivity(intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP )) }
        }
        activity.overridePendingTransition(R.anim.right_to_current,R.anim.current_to_left )
    }


    private fun RxjavaErrorHandling() {
        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            var error = e

            if (e is UndeliverableException) {
                error = e.cause
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