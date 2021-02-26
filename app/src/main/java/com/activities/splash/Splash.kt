package com.activities.splash

import android.util.Log
import com.application.GlobalApplication
import com.base.BaseActivity
import com.custom.CustomDialog
import com.error.ErrorManager
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Splash : BaseActivity<SplashBinding>(), SplashContract.SplashView {

    private lateinit var presenter:SplashPresenter

    override val layoutResID: Int = R.layout.splash

    override fun setOnCreate() {
        //디바이스별의 크기 얻기
        GlobalApplication.instance.getStandardSize(this)

        presenter = SplashPresenter().apply {
            view = this@Splash
            activity = this@Splash
        }

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val tokenCheck = JWTUtil.checkAccessToken()
                if (tokenCheck) {
                    Log.e("tokenCheck", tokenCheck.toString())
                    presenter.setUserInfo()
                }

                Log.e("버전체크", "버전체크 전")
                val check = versionCheck()
                withContext(Dispatchers.Main) {
                    if (check) {
                        CustomDialog.versionDialog(this@Splash)

                    } else {
                        presenter.moveActivity()
                    }
                    Log.e("버전체크", "버전체크 후")
                }
            }

        } catch (e: Exception) {
            Log.e(ErrorManager.JWT_ERROR, "splash-> ${e.message}")
        }

    }

    override fun destroyPresenter() {
        presenter.detach()
    }

    private suspend fun versionCheck():Boolean{
       return  presenter.versionCheck()
    }

    override fun getBindingObj(): SplashBinding {
        return binding
    }
}