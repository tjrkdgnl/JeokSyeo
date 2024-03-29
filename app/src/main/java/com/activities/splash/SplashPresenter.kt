package com.activities.splash

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.activities.main.MainActivity
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SplashPresenter : SplashContract.SplashPresenter {
    private val compositedisposable = CompositeDisposable()

    override val view: SplashContract.SplashView by lazy {
        viewObj!!
    }
    override var viewObj: SplashContract.SplashView? = null

    override lateinit var activity: Activity
    private var retrofit = ApiGenerator.retrofit.create(ApiService::class.java)
    private val handler = Handler(Looper.getMainLooper())


    /**
     * 2초뒤에 메인화면으로 이동. 시간을 지연시켜 액티비티를 전환하는 이유는 단순히 스플레쉬 화면을
     * 노출하는 데에 있다.
     */
    override fun moveActivity() {
        handler.postDelayed({
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        }, 2000)
    }

    override suspend fun setUserInfo(): Boolean = suspendCoroutine { coroutineResult ->

        compositedisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getUserInfo(
                    GlobalApplication.userBuilder.createUUID,
                    "Bearer " + GlobalApplication.userDataBase.getAccessToken()
                )
                .subscribeOn(Schedulers.io())
                .subscribe({ user ->
                    //유저정보 셋팅 app단에서 singleTon으로 관리하여 어느 클래스에서든 같은 데이터에 접근
                    GlobalApplication.userInfo = UserInfo.Builder().apply {
                        setProvider(user.data?.userInfo?.provider)
                        setNickName(user.data?.userInfo?.nickname ?: "")
                        setBirthDay(user.data?.userInfo?.birth ?: "1970-01-01")
                        setProfile(user.data?.userInfo?.profile)
                        setGender(user.data?.userInfo?.gender ?: "M")
                        setAddress("")
                        setLevel(user.data?.userInfo?.level ?: 0)
                        setAccessToken("Bearer " + GlobalApplication.userDataBase.getAccessToken())
                        coroutineResult.resume(true)        //suspend 끝내고 다음 로직 실행
                    }.build()
                }, { t ->
                    coroutineResult.resume(false)           //suspend 끝내고 다음 로직 실행
                    CustomDialog.networkErrorDialog(activity)
                    Log.e(ErrorManager.USERINFO, t.message.toString())
                })
        )
    }

    override suspend fun versionCheck(): Boolean = suspendCoroutine { coroutineResult ->
        val packageManager = activity.packageManager.getPackageInfo(
            activity.packageName,
            PackageManager.GET_META_DATA
        )
        val currentVersion = packageManager.versionName.split(".")
        var latestVersion = "1.0.0".split(".")

        compositedisposable.add(
            retrofit
                .getVersion(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    "android"
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.platform?.let { platform ->
                        if (platform == "android") {
                            it.data?.version?.let { version ->
                                latestVersion = version.split(".")

                                try {
                                    //메인 패치일 경우 바로 업데이트 안내
                                    if (latestVersion[0].toInt() > currentVersion[0].toInt()) {
                                        coroutineResult.resume(true)
                                    } else {
                                        if (latestVersion[0].toInt() == currentVersion[0].toInt()) {
                                            when {
                                                //마이너 버전체크
                                                latestVersion[1].toInt() > currentVersion[1].toInt() -> {
                                                    coroutineResult.resume(true)
                                                }
                                                //패치 버전체크
                                                latestVersion[2].toInt() > currentVersion[2].toInt() -> {
                                                    coroutineResult.resume(true)
                                                }
                                                else -> {
                                                    coroutineResult.resume(false)
                                                }
                                            }
                                        } else {
                                            coroutineResult.resume(false)
                                        }
                                    }

                                } catch (e: java.lang.Exception) {
                                    coroutineResult.resume(false)
                                    Log.e(ErrorManager.VERSION, e.message.toString())
                                    CustomDialog.networkErrorDialog(activity)
                                }
                            }
                        }
                    }
                }, { t ->
                    CustomDialog.networkErrorDialog(activity)
                    Log.e(ErrorManager.VERSION, t.message.toString())
                })
        )
    }

    override fun detach() {
        compositedisposable.dispose()
        viewObj = null
    }
}