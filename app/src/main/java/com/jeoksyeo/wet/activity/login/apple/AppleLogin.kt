package com.jeoksyeo.wet.activity.login.apple

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.jeoksyeo.wet.activity.login.Login
import com.error.ErrorManager
import com.google.android.gms.tasks.OnCompleteListener
import com.jeoksyeo.wet.activity.main.MainActivity
import com.jeoksyeo.wet.activity.setting.SettingActivity
import com.model.user.UserInfo
import com.nhn.android.naverlogin.OAuthLogin
import com.vuforia.engine.wet.R
import java.lang.Exception
import java.lang.StringBuilder

class AppleLogin(private val mContext:Context,private val activity: Activity) {
    private val provider = OAuthProvider.newBuilder("apple.com")
    private val pending = FirebaseAuth.getInstance().pendingAuthResult
    private var user: FirebaseUser? = null

    init {
        provider.addCustomParameter("locale", "ko_KR")
        provider.scopes = mutableListOf("email", "name")
    }

    fun loginExecute() {
        user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            //동일한 작업을 하고있는지 확인
            if (pending != null) {
                pending.addOnSuccessListener { authResult ->
                    //동일한 작업을 하고 있다면 여기서 정보들을 얻을 수 있음.
                    Log.d(ErrorManager.Apple_TAG, "checkPending:onSuccess:$authResult")

                }.addOnFailureListener { e ->
                    Log.w(ErrorManager.Apple_TAG, "checkPending:onFailure", e)
                }
            } else {
                //동일한 작업을 하고 있지 않을 때.
                // 본격적인 회원가입 절차 진행하면 됨.
                Log.d(ErrorManager.Apple_TAG, "pending: null")

                //firebase에 자신이 지정한 소셜 회사의 로그인 api를 진행
                FirebaseAuth.getInstance()
                    .startActivityForSignInWithProvider(activity, provider.build())
                    .addOnSuccessListener { authResult ->
                        try {
                            user = authResult.user!!
                            //access token
                            user!!.getIdToken(true) //   FirebaseAuth.getInstance().getAccessToken()와 동일
                                .addOnCompleteListener(activity) { task ->
                                    if (task.isSuccessful) {
                                        Log.e(
                                            ErrorManager.Apple_TAG,
                                            "accessToken: " + task.result?.token.toString()
                                        )

                                        Login.loginObj.setUserInfo("APPLE", task.result?.token)

                                    }
                                }
                        } catch (e: Exception) {
                            Log.e(ErrorManager.Apple_TAG, "user object is null")
                        }

                    }
            }.addOnFailureListener { e ->
                Log.e(ErrorManager.Apple_TAG, "activitySignIn:onFailure", e)
            }
        }
    }

    fun appleSignOut() {
        val dialog = Dialog(mContext, R.style.Theme_Dialog)
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
            Log.e("애플로그아웃",FirebaseAuth.getInstance().currentUser?.email.toString())
            FirebaseAuth.getInstance().signOut()
            GlobalApplication.userInfo.init()
            GlobalApplication.userDataBase.setAccessToken(null)
            GlobalApplication.userDataBase.setRefreshToken(null)
            GlobalApplication.userDataBase.setAccessTokenExpire(null)
            GlobalApplication.userDataBase.setRefreshToken(null)

            mContext.startActivity(Intent(mContext,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            (mContext as Activity).finish()
            Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

    fun appleDelete() {
        val dialog = Dialog(mContext, R.style.Theme_Dialog)
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
            FirebaseAuth.getInstance().currentUser?.delete()
                ?.addOnCompleteListener(activity, OnCompleteListener {
                    if (it.isSuccessful) {
                        //삭제 후 핸들링
                        //서버 자체에서 탈퇴를 진행하는 api도 실행하기
                        Log.e("애플삭제", FirebaseAuth.getInstance().currentUser?.email.toString())
                        GlobalApplication.userInfo.init()
                        GlobalApplication.userDataBase.setAccessToken(null)
                        GlobalApplication.userDataBase.setRefreshToken(null)
                        GlobalApplication.userDataBase.setAccessTokenExpire(null)
                        GlobalApplication.userDataBase.setRefreshToken(null)
                        Toast.makeText(mContext, "탈퇴완료 되었습니다.", Toast.LENGTH_SHORT).show()

                        mContext.startActivity(Intent(mContext,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        (mContext as Activity).finish()
                    }
                    else{
                        Toast.makeText(mContext, "탈퇴가 제대로 진행되지않았습니다.\n" +
                                "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()

                })?.addOnFailureListener {
                    Toast.makeText(mContext, "탈퇴가 제대로 진행되지않았습니다.\n" +
                            "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    Log.e("구글삭제 실패",it.message.toString())
                    dialog.dismiss() }
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }
}

