package com.activities.signup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.adapters.signup.SignUpViewPagerAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.activities.main.MainActivity
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.service.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SignUpPresenter : SignUpContract.BasePresenter {
    override lateinit var view: SignUpContract.BaseView
    override lateinit var activity: FragmentActivity
    private var compositDisposable =CompositeDisposable()
    private lateinit var networkUtil: NetworkUtil

    override fun setNetworkUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(activity)
            networkUtil.register()
        }
    }

    override fun initViewpager() {
      val  mutableList = mutableListOf<String>()
        mutableList.add(GlobalApplication.NICKNAME)

        activity.intent.let {
            val bundle = it.getBundleExtra(GlobalApplication.USER_BUNDLE)
            bundle?.let { bun ->
                if (!bun.getBoolean(GlobalApplication.BIRTHDAY, false)) mutableList.add(
                    GlobalApplication.BIRTHDAY
                )
                if (!bun.getBoolean(GlobalApplication.GENDER, false)) mutableList.add(
                    GlobalApplication.GENDER
                )
            }
        }
        mutableList.add("location")

        //progressbar init
        view.getBinding().signupHeader.signUpHeaderProgressbar.max = mutableList.size

        //viewPager2 init
        val signUpViewPagerAdapter = SignUpViewPagerAdapter(activity, mutableList)
        view.getBinding().viewPager2.adapter = signUpViewPagerAdapter
        view.getBinding().viewPager2.isUserInputEnabled = false //viewpager2 스와이프off
        view.getBinding().viewPager2.offscreenPageLimit = 1
    }

    @SuppressLint("HardwareIds")
    override fun signUp() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("디바이스 토큰 에러", task.exception?.message.toString())
            } else {
                //회원정보객체 셋팅
                GlobalApplication.userInfo = GlobalApplication.userBuilder.build()

                val userMap = GlobalApplication.userInfo.getMap()

                userMap["device_platform"] = "AOS"
                userMap["device_model"] = Build.MODEL
                userMap["device_id"] =Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
                userMap["device_token"] = task.result!!

                compositDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .signUp(GlobalApplication.userBuilder.createUUID, userMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //내장 디비에 토큰 값들 저장
                        GlobalApplication.userDataBase.setAccessToken(it.data?.token?.accessToken)
                        GlobalApplication.userDataBase.setRefreshToken(it.data?.token?.refreshToken)

                        //토큰 안에 들어있는 만료시간 구해서 내장디비에 저장
                        JWTUtil.decodeAccessToken(it.data?.token?.accessToken.toString())
                        JWTUtil.decodeRefreshToken(it.data?.token?.refreshToken.toString())

                        //토큰을 갖는 user객체 셋팅
                        GlobalApplication.userInfo = GlobalApplication.userBuilder
                            .setAccessToken("Bearer ${GlobalApplication.userDataBase.getAccessToken()}")
                            .setLevel(1)
                            .build()

                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish()
                    }, { t: Throwable ->
                        CustomDialog.networkErrorDialog(activity)
                        t.stackTrace }))
            }
        }
    }

    override fun hideKeypad(activity: Activity, buttonName: Button) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(buttonName.windowToken, 0)
    }

    override fun detachView() {
        compositDisposable.dispose()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }
    }
}