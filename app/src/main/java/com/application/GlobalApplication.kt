package com.application

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.common.KakaoSdk
import com.model.user.UserInfo
import com.sharedpreference.UserDB
import com.vuforia.engine.wet.R
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GlobalApplication : MultiDexApplication() {
    var activityClass:Class<*>? =null
    var device_width =0f
    var device_height = 0f
    private var activityBackground = false
    private var toastView:View? =null
    private val banWord =  listOf("개발","운영","관리자","적셔")
    private val typeList = listOf("TR", "BE", "WI", "FO", "SA")
    private val ratedList = listOf("ALL", "TR", "BE", "WI", "SA", "FO")
    private val levelList = listOf(
        "마시는 척 하는 사람", "술을 즐기는 사람"
        , "술독에 빠진 사람", "주도를 수련하는 사람", "술로 해탈한 사람"
    )

    lateinit var context:Context

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //카카오톡 로그인 init
        KakaoSdk.init(this, getString(R.string.kakaoNativeKey))

        //유저정보 셋팅
        userBuilder = UserInfo.Builder()
        userInfo = UserInfo()
        userDataBase = UserDB.getInstance(this)

        //rx에러 핸들링
        RxjavaErrorHandling()

        //오류보고 툴
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    companion object {
        //싱글턴 객체 생성
        lateinit var instance : GlobalApplication

        //유저 정보
        lateinit var userInfo: UserInfo
        lateinit var userBuilder: UserInfo.Builder
        lateinit var userDataBase: UserDB

        //디바이스 사이즈
        const val NICKNAME = "nickname"
        const val BIRTHDAY = "birth"
        const val GENDER = "gender"
        const val LOCATION = "location"
        const val USER_ID = "user_id"
        const val OAUTH_PROVIDER = "oauth_provider"
        const val OAUTH_TOKEN = "oauth_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val ADDRESS = "address"
        const val ACTIVITY_HANDLING = "activity_handling"
        const val USER_BUNDLE = "userBundle"
        const val ACTIVITY_HANDLING_BUNDLE = "activityHandling_bundle"

        const val CATEGORY_BUNDLE = "category_bundle"
        const val ALCHOL_BUNDLE = "alcohol"
        const val DEEP_LINK_ALCOHOL = "deepLink_alcohol"
        const val CATEGORY_SIZE = 5
        const val MOVE_TYPE = "type"
        const val PAGINATION_SIZE = 20
        const val DEFAULT_SORT = "review"
        const val MOVE_ALCHOL = "alcohol Data"
        const val ALCHOL_LIKE = "likeAndDisLike"
        const val ACTIVITY_HANDLING_MAIN = 0
        const val ACTIVITY_HANDLING_DETAIL = 1
        const val ACTIVITY_HANDLING_CATEGORY = 2
        const val MOVE_MY_COMMENT = "my comment"

        const val DETAIL_NO_REVIEW = 0
        const val DETAIL_REVIEW = 1
        const val DETAIL_MORE_REVIEW = 2

        const val COMPONENT_DEFAULT = 0
        const val COMPONENT_RECYCLERVIEW = 1
        const val COMPONENT_SRM = 2

        const val AGREEMENT = "agreement"
        const val AGREEMENT_INFO = "agreement_info"

        const val PAGE_REVIEW_COUNT =3
    }

    fun setActivityBackground(check:Boolean){
        activityBackground = check
    }

    fun getActivityBackground() = activityBackground

    fun getScreenSize(activity: Activity):Point{
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        return size
    }

    fun getStandardSize(activity: Activity){
        val screenSize = getScreenSize(activity)
        val density = activity.resources.displayMetrics.density


        device_width = (screenSize.x / density)
        device_height = (screenSize.y / density)

        Log.e("width",device_width.toString())
        Log.e("height",device_height.toString())
    }


   fun getToastView() :View?{
        if(toastView ==null){
            toastView =  LayoutInflater.from(this).inflate(R.layout.custom_toast,null)
        }

       return toastView
    }


    fun getDate(utc:Long):String{
        val date = Date(utc)

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        return simpleDateFormat.format(date)
    }

    fun getAlcoholType(positon: Int) = typeList[positon]

    fun getRatedType(positon: Int) = ratedList[positon]

    fun getLevelName(positon: Int) = levelList[positon]

    fun getBanWordList() = banWord


    fun checkCount(value: Int, count: Int = 0): String {
        return if (value >= 10000) "9999+"
        else if (value + count < 0) "0"
        else
            (value + count).toString()
    }

    fun keyPadSetting(editText: EditText, context: Context, hide: Boolean = true) {
        if (hide) {
            val inputMethodManager =
                context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        } else {
            editText.post(Runnable {
                editText.requestFocus()
                val inputMethodManager =
                    context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(editText, 0)
            })
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun removeEditextFocus(editText: EditText, view: View) {
        view.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val outRect = Rect()
                editText.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    editText.clearFocus()
                    keyPadSetting(editText, view.context)
                }
            }
            false
        }
    }

    //액티비티 전환
    fun moveActivity(
        context: Context,
        activityClass: Class<*>,
        intentFlags: Int = 0,
        bundle: Bundle? = null,
        bundleFlag: String? = null,
        animationFlag: Int = 0
    ) {
        val activity = context as Activity
        val intent = Intent(context, activityClass)

        bundle?.let { bun ->
            bundleFlag?.let { flag ->
                intent.putExtra(flag, bun)
            }
        }

        when (intentFlags) {
            0 -> {
                activity.startActivity(intent)
            }

            Intent.FLAG_ACTIVITY_SINGLE_TOP -> {
                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }

            Intent.FLAG_ACTIVITY_CLEAR_TOP -> {
                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        when (animationFlag) {
            0 -> {
                activity.overridePendingTransition(R.anim.right_to_current, R.anim.current_to_left)
            }

            1 -> {
                activity.overridePendingTransition(
                    R.anim.right_to_current,
                    R.anim.current_to_current
                )
            }
        }

    }




    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun RxjavaErrorHandling() {
        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            var error = e
            if (e is UndeliverableException) {
               error= e.cause
            }
            if (e is IOException) {
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            Log.e("RxJava_HOOK", "Undeliverable exception received, not sure what to do" + error?.message)
        }
    }
}