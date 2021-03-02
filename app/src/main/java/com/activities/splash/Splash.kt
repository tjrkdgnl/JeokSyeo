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
            viewObj = this@Splash
            activity = this@Splash
        }

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val tokenCheck = JWTUtil.checkAccessToken()

                //토큰이 유효한지 확인 후, 유저 정보 셋팅 유무 결정
                if (tokenCheck) {
                    Log.e("tokenCheck", tokenCheck.toString())
                    presenter.setUserInfo()
                }

                Log.e("버전체크", "버전체크 전")
                val check = versionCheck()
                withContext(Dispatchers.Main) {
                    if (check) {
                        //업데이트 필요성이 있는 경우, 스토어 안내 다이얼로그 띄움
                        CustomDialog.versionDialog(this@Splash)

                    } else {
                        //업데이트가 필요없는 경우, 메인화면으로 이동
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