package com.jeoksyeo.wet.activity.login.naver

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.application.GlobalApplication
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.main.MainActivity
import com.jeoksyeo.wet.activity.signup.SignUp
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class NaverLogin(private val mContext: Context) {
    val instance = OAuthLogin.getInstance()
    var naverLoginHandler: OAuthLoginHandler
    lateinit var executeProgressBar: (Boolean) -> Unit
    private var disposable: Disposable? = null

    init {
        instance.init(
            mContext,
            "j9Wy3_RT3u8uP_9nJJ_U",
            "KwEVgBIcv2",
            "적셔"
        )

        @SuppressLint("HandlerLeak")
        naverLoginHandler = object : OAuthLoginHandler() {
            override fun run(sccess: Boolean) {
                if (sccess) {
                    val accessToken = OAuthLogin.getInstance().getAccessToken(mContext)
                    try {
                        //포그라운드로 동기화 처리. -> 값을 받을 때까지 기다림
                        CoroutineScope(Dispatchers.IO).launch {
                            val userJson = OAuthLogin.getInstance()
                                .requestApi(
                                    mContext,
                                    accessToken,
                                    "https://openapi.naver.com/v1/nid/me"
                                )!! //->  !!은 none null을 의미

                            Login.loginObj.setUserInfo("NAVER", accessToken)

//                            parsingUserInfo(userJson)
                        }
                    } catch (e: Exception) {
                        val message = e.message ?: e.stackTrace
                        Log.e(ErrorManager.Naver_TAG, message.toString())
                        executeProgressBar(false)
                    }

                } else {
                    executeProgressBar(false)
                    Log.e(
                        ErrorManager.Naver_TAG,
                        OAuthLogin.getInstance().getLastErrorCode(mContext).code
                    )
                }
            }

        }
    }

    private fun parsingUserInfo(userJson: String) {
        val jsonObject = JSONObject(userJson)
        val response = jsonObject.getJSONObject("response")

        Log.e("response", response.toString())


        mContext.startActivity(Intent(mContext, SignUp::class.java))
    }

    fun naverLogOut() {
        val dialog = Dialog(mContext, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val contents = dialog.findViewById<TextView>(R.id.dialog_contents)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        okButton.text = "로그아웃"
        contents.setText(R.string.logout_msg)

        okButton.setOnClickListener { v: View? ->
            instance.logout(mContext)
            GlobalApplication.userInfo.init()
            GlobalApplication.userDataBase.setAccessToken(null)
            GlobalApplication.userDataBase.setRefreshToken(null)
            GlobalApplication.userDataBase.setAccessTokenExpire(0)
            GlobalApplication.userDataBase.setRefreshTokenExpire(0)


            mContext.startActivity(Intent(mContext, MainActivity::class.java))
            if (mContext is MainActivity) {
                mContext.finish()
                mContext.overridePendingTransition(R.anim.right_to_current, R.anim.current_to_left)
            }
            Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    fun naverDelete() {
        val dialog = Dialog(mContext, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val contents = dialog.findViewById<TextView>(R.id.dialog_contents)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        okButton.text = "회원탈퇴"
        contents.setText(R.string.delete_app)

        okButton.setOnClickListener {
            disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                .deleteUser(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.result?.let { result ->
                        if (result == "SUCCESS") {

                            CoroutineScope(Dispatchers.IO).launch {
                                instance.logoutAndDeleteToken(mContext)
                            }

                            //서버 자체에서 탈퇴를 진행하는 api도 실행하기
                            GlobalApplication.userInfo.init()
                            GlobalApplication.userDataBase.setAccessToken(null)
                            GlobalApplication.userDataBase.setRefreshToken(null)
                            GlobalApplication.userDataBase.setAccessTokenExpire(0)
                            GlobalApplication.userDataBase.setRefreshTokenExpire(0)

                            Toast.makeText(mContext, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()

                            mContext.startActivity(
                                Intent(
                                    mContext,
                                    MainActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
                            if (mContext is Activity) {
                                mContext.finish()
                                mContext.overridePendingTransition(
                                    R.anim.right_to_current,
                                    R.anim.current_to_left
                                )
                            }
                            disposable?.dispose()
                            dialog.dismiss()
                        }
                    }
                }, { t ->
                    dialog.dismiss()
                    Toast.makeText(mContext, "재 로그인 후, 다시 실행해주세요.", Toast.LENGTH_SHORT).show()
                    Log.e(ErrorManager.DELETE_USER, t.message.toString())
                })
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
    }


    fun naverUnlink(){
        CoroutineScope(Dispatchers.IO).launch {
            instance.logoutAndDeleteToken(mContext)
        }
    }
}
