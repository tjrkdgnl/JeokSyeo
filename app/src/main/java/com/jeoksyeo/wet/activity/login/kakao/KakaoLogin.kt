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
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.main.MainActivity
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class KakaoLogin(private val context: Context) {
    val TAG = "KAKAO_USERINFO"
    val instance = LoginClient.instance
    val userInfo = UserApiClient.instance
    private  var disposable:Disposable? =null

    lateinit var executeProgressBar:(Boolean) ->Unit

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            executeProgressBar(false)
            Log.e(ErrorManager.Kakao_TAG, "로그인 실패 ->"+ error.message.toString())
        } else if (token != null) {
            Log.e(ErrorManager.Kakao_TAG, "로그인 성공 ${token.accessToken}")

            Login.loginObj.setUserInfo("KAKAO", token.accessToken)
        }
    }

    fun getUserInfo(context: Context, token: String) {
        userInfo.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패->"+ error.message.toString())
            } else if (user != null) {

            }
        }
    }

    fun kakaoLogOut() {
        val dialog = Dialog(context, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_twobutton)
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
                    Log.e(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }

            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            GlobalApplication.userInfo.init()
            GlobalApplication.userDataBase.setAccessToken(null)
            GlobalApplication.userDataBase.setRefreshToken(null)
            GlobalApplication.userDataBase.setAccessTokenExpire(0)
            GlobalApplication.userDataBase.setRefreshTokenExpire(0)


            context.startActivity(Intent(context, MainActivity::class.java))
            if(context is MainActivity){
                context.finish()
            }
            dialog.dismiss()
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    fun kakaoDelete() {
        val dialog = Dialog(context, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_twobutton)
        dialog.show()
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val contents = dialog.findViewById<TextView>(R.id.dialog_contents)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        okButton.text = "회원탈퇴"
        contents.setText(R.string.delete_app)

        okButton.setOnClickListener {
            disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                .deleteUser(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.result?.let { result->
                        if(result =="SUCCESS"){
                            userInfo.unlink { error ->
                                if (error != null) {
                                    Log.e(TAG, "연결 끊기 실패", error)
                                    Toast.makeText(context, "재 로그인 후, 다시 진행해주세요.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                                    //서버 자체에서 탈퇴를 진행하는 api도 실행하기
                                    GlobalApplication.userInfo.init()
                                    GlobalApplication.userDataBase.setAccessToken(null)
                                    GlobalApplication.userDataBase.setRefreshToken(null)
                                    GlobalApplication.userDataBase.setAccessTokenExpire(0)
                                    GlobalApplication.userDataBase.setRefreshTokenExpire(0)

                                    context.startActivity(Intent(context,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                                    if(context is Activity){
                                        context.finish()
                                        context.overridePendingTransition(R.anim.right_to_current,R.anim.current_to_left )
                                    }
                                    disposable?.dispose()
                                    Toast.makeText(context, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },{t->
                    dialog.dismiss()
                    Toast.makeText(context, "재 로그인 후, 다시 실행해주세요.", Toast.LENGTH_SHORT).show()
                    Log.e(ErrorManager.DELETE_USER,t.message.toString())})
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    fun kakaoUnlink(){
        userInfo.unlink { error ->
            error?.let {
                Log.e(ErrorManager.Kakao_TAG,error.message.toString())
            }
        }
    }
}