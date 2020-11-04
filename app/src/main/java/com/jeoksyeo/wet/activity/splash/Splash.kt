package com.jeoksyeo.wet.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.main.MainActivity
import com.jeoksyeo.wet.activity.test.TestActivity
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SplashBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Splash : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: SplashBinding = DataBindingUtil.setContentView(this, R.layout.splash)

        val handler = Handler()

        Glide.with(this)
            .load(R.drawable.splash_logo_gif)
            .into(binding.splashImage)


        JWTUtil.settingUserInfo(true)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        GlobalApplication.device_width = displayMetrics.widthPixels
        GlobalApplication.device_height = displayMetrics.heightPixels

        handler.postDelayed(Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }


}