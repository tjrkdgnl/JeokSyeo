package com.jeoksyeo.wet.activity.login.kakao

import android.content.Context
import android.util.Log
import com.jeoksyeo.wet.activity.login.Login
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.error.ErrorManager

class KakaoLogin(private val context: Context) {
    val TAG = "KAKAO_USERINFO"
    val instance = LoginClient.instance
    val userInfo = UserApiClient.instance

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(ErrorManager.Kakao_TAG, "로그인 실패", error)
        } else if (token != null) {
            Log.i(ErrorManager.Kakao_TAG, "로그인 성공 ${token.accessToken}")

            Log.e("refreshToken",token.refreshToken)
            Log.e("accessToken",token.accessToken)
            Login.loginObj.setUserInfo("KAKAO",token.accessToken)
        }
    }

    fun getUserInfo(context: Context,token:String) {
        userInfo.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {

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