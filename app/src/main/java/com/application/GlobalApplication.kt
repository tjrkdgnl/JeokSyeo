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
    private val ratedList = listOf<String>("ALL","TR", "BE", "WI", "SA", "FO")
    private val levelList = listOf<String>("마시는 척 하는 사람","술을 즐기는 사람"
        ,"술독에 빠진 사람","주도를 수련하는 사람","술로 해탈한 사람")

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
        const val ALCHOL_BUNDLE = "alcohol"
        const val CATEGORY_SIZE = 5
        const val MOVE_TYPE = "type"
        const val PAGINATION_SIZE= 20
        const val DEFAULT_SORT = "like"
        const val MOVE_ALCHOL = "alcohol Data"
        const val ALCHOL_LIKE = "likeAndDisLike"
        const val ACTIVITY_HANDLING_MAIN = 0
        const val ACTIVITY_HANDLING_DETAIL = 1
        const val ACTIVITY_HANDLING_CATEGORY = 2
        const val MOVE_MY_COMMENT ="my comment"

        const val DETAIL_NO_REVIEW = 0
        const val DETAIL_REVIEW = 1
        const val DETAIL_MORE_REVIEW = 2

        const val COMPONENT_DEFAULT=0
        const val COMPONENT_RECYCLERVIEW=1
        const val COMPONENT_SRM=2
    }

    fun getAlcoholType(positon: Int) = typeList[positon]

    fun getRatedType(positon: Int) = ratedList[positon]

    fun getLevelName(positon: Int) = levelList[positon]

    fun checkCount(value:Int,count:Int=0)=  if(value >=10000) "9999+" else (value+count).toString()

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