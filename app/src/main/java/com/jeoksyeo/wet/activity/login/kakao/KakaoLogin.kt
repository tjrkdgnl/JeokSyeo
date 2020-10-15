package com.jeoksyeo.wet.activity.login.kakao

import android.content.Context
import android.content.Intent
import android.util.Log
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.signup.SignUp
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import error.ErrorManager

class KakaoLogin(private val context: Context) {
    val TAG = "KAKAO_USERINFO"
    val instance = LoginClient.instance
    val userInfo = UserApiClient.instance

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(ErrorManager.Kakao_TAG, "로그인 실패", error)
        } else if (token != null) {
            Log.i(ErrorManager.Kakao_TAG, "로그인 성공 ${token.accessToken}")
            getUserInfo(context)

            Log.e("refreshToken",token.refreshToken)
            Log.e("accessToken",token.accessToken)

        }
    }

    fun getUserInfo(context: Context) {
        userInfo.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {

                val gender = user.kakaoAccount?.gender?.name.toString()[0].toString()

                Login.loginObj.setUserInfo("KAKAO",user.id.toString(),"카톡로그인"
                    ,user.kakaoAccount?.email,"1994-08-18",gender,"123")
            }
        }
    }

    fun kakaoLogOut() {
        userInfo.logout { error ->
            if (error != null) {
                Log.e(ErrorManager.Kakao_TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    fun kakaoDelete() {
        userInfo.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연결 끊기 실패", error)
            } else {
                Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

}