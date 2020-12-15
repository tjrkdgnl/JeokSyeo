package com.jeoksyeo.wet.activity.splash

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Splash : AppCompatActivity(), SplashContract.BaseView {

    private lateinit var presenter:SplashPresenter
    private lateinit var binding: SplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash)

        //디바이스별의 크기 얻기
        GlobalApplication.instance.getStandardSize(this)

        presenter = SplashPresenter().apply {
            view = this@Splash
            activity = this@Splash
        }

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val tokenCheck = JWTUtil.checkToken()
                if (tokenCheck) {
                    Log.e("tokenCheck", tokenCheck.toString())
                    presenter.setUserInfo()
                }

                Log.e("버전체크", "버전체크 전")
                val check = versionCheck()
                withContext(Dispatchers.Main) {
                    if (check) {
                        presenter.moveActivity()
                    } else {
                        CustomDialog.versionDialog(this@Splash)
                    }
                    Log.e("버전체크", "버전체크 후")
                }
            }

        } catch (e: Exception) {
            Log.e(ErrorManager.JWT_ERROR, "splash-> ${e.message}")
        }

    }

    private suspend fun versionCheck():Boolean{
       return  presenter.versionCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun getBinding(): SplashBinding {
        return binding
    }
}