package com.jeoksyeo.wet.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.main.MainActivity
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SplashBinding
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Splash : AppCompatActivity() {
    private  var disposable:Disposable? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: SplashBinding = DataBindingUtil.setContentView(this, R.layout.splash)

        val handler = Handler()

        Glide.with(this)
            .load(R.drawable.splash_logo_gif)
            .into(binding.splashImage)

        try{
            if(JWTUtil.settingUserInfo()){
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getUserInfo(GlobalApplication.userBuilder.createUUID, "Bearer " + GlobalApplication.userDataBase.getAccessToken())
                    .subscribeOn(Schedulers.io())
                    .subscribe({user->
                        GlobalApplication.userInfo = UserInfo.Builder().apply {
                            setProvider("NAVER")
                            setNickName(user.data?.userInfo?.nickname ?: "")
                            setBirthDay(user.data?.userInfo?.birth ?: "1970-01-01")
                            setProfile(user.data?.userInfo?.profile)
                            setGender(user.data?.userInfo?.gender ?: "M")
                            setAddress("") //추후에 셋팅하기
                            setLevel(user.data?.userInfo?.level ?: 0)
                            setAccessToken("Bearer " + GlobalApplication.userDataBase.getAccessToken())
                        }.build()
                    },{})
            }
        }
        catch (e:Exception){
            Log.e(ErrorManager.JWT_ERROR,"splash-> ${e.message}")
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        GlobalApplication.device_width = displayMetrics.widthPixels
        GlobalApplication.device_height = displayMetrics.heightPixels

        handler.postDelayed( {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}