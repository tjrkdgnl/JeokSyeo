package com.jeoksyeo.wet.activity.login.google

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.application.GlobalApplication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.vuforia.engine.wet.R
import com.error.ErrorManager
import com.google.firebase.auth.FirebaseAuth
import com.jeoksyeo.wet.activity.main.MainActivity
import com.model.user.UserInfo
import com.nhn.android.naverlogin.OAuthLogin

class GoogleLogin(private val mContext: Context, private val activity: Activity) {
    val gso: GoogleSignInOptions
    val instance: GoogleSignInClient

    init {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(mContext.getString(R.string.googleWebApplicationId))
            .requestEmail()
            .build()

        instance = GoogleSignIn.getClient(mContext, gso)
    }

    fun googleLogOut() {
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
            Log.e("구글로그아웃",FirebaseAuth.getInstance().currentUser?.email.toString())
            FirebaseAuth.getInstance().signOut()
            GlobalApplication.userInfo.init()
            if(mContext is MainActivity)
                mContext.refresh()
            else{
                //카테고리 화면에서 초기화 진행
            }
            Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

    fun googleDelete() {

        FirebaseAuth.getInstance().currentUser?.delete()
            ?.addOnCompleteListener(activity, OnCompleteListener {
                if (it.isSuccessful) {
                    //삭제 후 핸들링
                    Log.e("구글삭제",FirebaseAuth.getInstance().currentUser?.email.toString())
                }
            })?.addOnFailureListener {
                it.stackTrace
            }
    }
}