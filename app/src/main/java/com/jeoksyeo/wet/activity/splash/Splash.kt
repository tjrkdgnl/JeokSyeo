package com.jeoksyeo.wet.activity.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.main.MainActivity
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SplashBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.internal.wait

class Splash : AppCompatActivity() {
    private var compositedisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: SplashBinding = DataBindingUtil.setContentView(this, R.layout.splash)
        val handler = Handler()

        try {
            if (JWTUtil.settingUserInfo()) {
                compositedisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .getUserInfo(
                        GlobalApplication.userBuilder.createUUID,
                        "Bearer " + GlobalApplication.userDataBase.getAccessToken()
                    )
                    .subscribeOn(Schedulers.io())
                    .subscribe({ user ->
                        GlobalApplication.userInfo = UserInfo.Builder().apply {
                            setProvider(user.data?.userInfo?.provider)
                            setNickName(user.data?.userInfo?.nickname ?: "")
                            setBirthDay(user.data?.userInfo?.birth ?: "1970-01-01")
                            setProfile(user.data?.userInfo?.profile)
                            setGender(user.data?.userInfo?.gender ?: "M")
                            setAddress("") //추후에 셋팅하기
                            setLevel(user.data?.userInfo?.level ?: 0)
                            setAccessToken("Bearer " + GlobalApplication.userDataBase.getAccessToken())
                        }.build()
                    }, {})
                )
            }
        } catch (e: Exception) {
            Log.e(ErrorManager.JWT_ERROR, "splash-> ${e.message}")
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        GlobalApplication.device_width = displayMetrics.widthPixels
        GlobalApplication.device_height = displayMetrics.heightPixels

      val run=  runBlocking {
            CoroutineScope(Dispatchers.Main).async {
                val check = getCheck()
                Log.e("바깥check",check.toString())
                withContext(Dispatchers.Main){
                    if(check){
                        Log.e("check",check.toString())
                        handler.postDelayed({
                            startActivity(Intent(this@Splash, MainActivity::class.java))
                            finish()
                        }, 3000)
                    }
                    else{
                        CustomDialog.versionDialog(this@Splash)
                    }
                }
            }
        }
    }

    private suspend fun getCheck():Boolean = coroutineScope {
        val check = async {
            delay(1000)
            versionCheck()
        }
        check.await()
    }

    private fun versionCheck(): Boolean {
        var check = true

        val packageManager = this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_META_DATA)
        val currentVersion = packageManager.versionName.split(".")
        var latestVersion = "1.0.0".split(".")

        compositedisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getVersion(GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(), "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.platform?.let { platform ->
                    if (platform == "android") {
                        it.data?.version?.let { version ->
                            latestVersion = version.split(".")

                            try {
                                //메이저 버전체크
                                check = when {
                                    latestVersion[0].toInt() > currentVersion[0].toInt() -> {
                                        false
                                    }
                                    //마이너 버전체크
                                    latestVersion[1].toInt() > currentVersion[1].toInt() -> {
                                        false
                                    }
                                    //패치 버전체크
                                    latestVersion[2].toInt() > currentVersion[2].toInt() -> {
                                        false
                                    }
                                    else -> {
                                        true
                                    }
                                }

                                Log.e("check",check.toString())
                            } catch (e: java.lang.Exception) {
                                Log.e(ErrorManager.VERSION, e.message.toString())
                                Toast.makeText(this, "버전을 확인하는데 문제가 생겼습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }, { t ->
                Log.e(ErrorManager.VERSION, t.message.toString())
            })
        )
        return check
    }

    override fun onDestroy() {
        super.onDestroy()
        compositedisposable.dispose()
    }
}