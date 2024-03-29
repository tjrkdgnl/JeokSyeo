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
import com.activities.main.MainActivity
import com.adapters.signup.SignUpViewPagerAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SignUpPresenter : SignUpContract.SignUpPresenter {
    override val view: SignUpContract.SignUpView by lazy {
        viewObj!!
    }
    override var viewObj: SignUpContract.SignUpView? =null

    override lateinit var activity: Activity
    private var compositDisposable =CompositeDisposable()


    override fun initViewpager() {
      val  mutableList = mutableListOf<String>()
        //유저로부터 기입받아야 할 값들을 순서대로 리스트에 담는다.
        mutableList.add(GlobalApplication.NICKNAME)

        //소셜 로그인으로부터 받은 정보가 있는지 판단
        //만약 받았다면 해당 부분은 유저로부터 기입받을 필요가 없으므로 생략
        // 받지 않았다면 유저로부터 기입받아야하므로 추가
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
        //유저가 살고 있는 동네 위치
        mutableList.add("location")

        //progressbar init
        view.getBindingObj().signupHeader.signUpHeaderProgressbar.max = mutableList.size

        //viewPager2 init
        val signUpViewPagerAdapter = SignUpViewPagerAdapter((activity as FragmentActivity), mutableList)
        view.getBindingObj().viewPager2.adapter = signUpViewPagerAdapter
        view.getBindingObj().viewPager2.isUserInputEnabled = false //스와이프off
        view.getBindingObj().viewPager2.offscreenPageLimit = 1
    }

    @SuppressLint("HardwareIds")
    override fun signUp() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("디바이스 토큰 에러", task.exception?.message.toString())
            } else {
                val userMap = GlobalApplication.userBuilder.getMap()

                //추후에 푸시알람을 위해 유저의 디바이스 정보를 같이 전송
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

    override fun hideKeypad( buttonName: Button) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(buttonName.windowToken, 0)
    }

    override fun detachView() {
        compositDisposable.dispose()
        viewObj =null
    }
}