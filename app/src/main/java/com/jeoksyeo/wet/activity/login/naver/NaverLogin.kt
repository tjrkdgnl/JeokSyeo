package com.jeoksyeo.wet.activity.login.naver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jeoksyeo.wet.activity.login.SignUp
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import error.ErrorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class NaverLogin(private val mContext: Context) {
    val instance = OAuthLogin.getInstance()
    var naverLoginHandler: OAuthLoginHandler

    init {
        instance.init(
            mContext,
            "DJjATgFQSG8oIDsiCZ5i",
            "ZHwdISpL_X",
            "JeokSyeo"
        )

        @SuppressLint("HandlerLeak")
        naverLoginHandler = object : OAuthLoginHandler() {
            override fun run(sccess: Boolean) {
                if (sccess) {
                    val accessToken = OAuthLogin.getInstance().getAccessToken(mContext)
                    val refreshToken = OAuthLogin.getInstance().getRefreshToken(mContext)
                    val expireAt = OAuthLogin.getInstance().getExpiresAt(mContext)
                    try {
                        //포그라운드로 동기화 처리. -> 값을 받을 때까지 기다림
                        CoroutineScope(Dispatchers.IO).launch {
                            val userJson = OAuthLogin.getInstance()
                                .requestApi(
                                    mContext,
                                    accessToken,
                                    "https://openapi.naver.com/v1/nid/me"
                                )!! //->  !!은 none null을 의미

                            parsingUserInfo(userJson)
                        }
                    } catch (e: Exception) {
                        val message = e.message ?: e.stackTrace
                        Log.e(ErrorManager.Naver_TAG, message.toString() )
                    }

                } else {
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

        val email = response.getString("email")
        val gender = response.getString("gender")
        val age = response.getString("age")

        Log.e(ErrorManager.Naver_TAG, email)
        mContext.startActivity(Intent(mContext,SignUp::class.java))
    }

    fun naverLogOut(){
        instance.logout(mContext)
    }

    fun naverDelete(){
        val success = instance.logoutAndDeleteToken(mContext)

        if(success){

        }
        else{

        }
    }
}
