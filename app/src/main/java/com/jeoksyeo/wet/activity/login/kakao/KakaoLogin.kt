package com.jeoksyeo.wet.activity.login.kakao

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
import com.jeoksyeo.wet.activity.login.Login
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.main.MainActivity
import com.vuforia.engine.wet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KakaoLogin(private val context: Context) {
    val TAG = "KAKAO_USERINFO"
    val instance = LoginClient.instance
    val userInfo = UserApiClient.instance

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(ErrorManager.Kakao_TAG, "로그인 실패", error)
        } else if (token != null) {
            Log.i(ErrorManager.Kakao_TAG, "로그인 성공 ${token.accessToken}")

            Log.e("refreshToken", token.refreshToken)
            Log.e("accessToken", token.accessToken)
            Login.loginObj.setUserInfo("KAKAO", token.accessToken)
        }
    }

    fun getUserInfo(context: Context, token: String) {
        userInfo.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {

            }
        }
    }

    fun kakaoLogOut() {
        val dialog = Dialog(context, R.style.Theme_Dialog)
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
            userInfo.logout { error ->
                if (error != null) {
                    Log.e(ErrorManager.Kakao_TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                } else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }

            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            GlobalApplication.userInfo.init()
            GlobalApplication.userDataBase.setAccessToken(null)
            GlobalApplication.userDataBase.setAccessTokenExpire(null)
            GlobalApplication.userDataBase.setRefreshTokenExpire(null)


            context.startActivity(Intent(context,MainActivity::class.java))
            if(context is MainActivity){
                context.finish()
                context.overridePendingTransition(R.anim.translation_x_right,R.anim.not_translation)
            }
            dialog.dismiss()
        }

        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

    fun kakaoDelete() {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val contents = dialog.findViewById<TextView>(R.id.dialog_contents)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        okButton.text = "회원탈퇴"
        contents.setText(R.string.delete_app)

        okButton.setOnClickListener { v: View? ->
            userInfo.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "연결 끊기 실패", error)
                    Toast.makeText(context, "탈퇴가 제대로 진행되지 않았습니다.\n " +
                            "다시 진행해 주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                    //서버 자체에서 탈퇴를 진행하는 api도 실행하기
                    GlobalApplication.userInfo.init()
                    GlobalApplication.userDataBase.setAccessToken(null)
                    GlobalApplication.userDataBase.setAccessTokenExpire(null)
                    GlobalApplication.userDataBase.setRefreshTokenExpire(null)

                    context.startActivity(Intent(context,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    if(context is Activity){
                        context.finish()
                        context.overridePendingTransition(R.anim.translation_x_right,R.anim.not_translation)
                    }
                    Toast.makeText(context, "탈퇴완료 되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.dismiss()
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }
}