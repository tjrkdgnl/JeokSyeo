package com.jeoksyeo.wet.activity.login.google

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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.vuforia.engine.wet.R
import com.google.firebase.auth.FirebaseAuth
import com.jeoksyeo.wet.activity.main.MainActivity
import com.service.ApiGenerator
import com.service.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class GoogleLogin(private val mContext: Context, private val activity: Activity) {
    private  var disposable: Disposable? =null
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
            Log.e("구글로그아웃", FirebaseAuth.getInstance().currentUser?.email.toString())
            FirebaseAuth.getInstance().signOut()
            GlobalApplication.userInfo.init()
            GlobalApplication.userDataBase.setAccessToken(null)
            GlobalApplication.userDataBase.setRefreshToken(null)
            GlobalApplication.userDataBase.setAccessTokenExpire(0)
            GlobalApplication.userDataBase.setRefreshTokenExpire(0)

            mContext.startActivity(Intent(mContext, MainActivity::class.java))
            if (mContext is MainActivity) {
                mContext.finish()
                mContext.overridePendingTransition(R.anim.right_to_current,R.anim.current_to_left )
            }
            Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

    fun googleDelete() {
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
                        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                            .deleteUser(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                it.data?.result?.let { result->
                                    if(result =="SUCCESS"){
                                        //삭제 후 핸들링
                                        //서버 자체에서 탈퇴를 진행하는 api도 실행하기
                                        Log.e("구글삭제", FirebaseAuth.getInstance().currentUser?.email.toString())
                                        GlobalApplication.userInfo.init()
                                        GlobalApplication.userDataBase.setAccessToken(null)
                                        GlobalApplication.userDataBase.setRefreshToken(null)
                                        GlobalApplication.userDataBase.setAccessTokenExpire(0)
                                        GlobalApplication.userDataBase.setRefreshTokenExpire(0)
                                        Toast.makeText(mContext, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()

                                        mContext.startActivity(
                                            Intent(mContext, MainActivity::class.java).addFlags(
                                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            )
                                        )
                                        if (mContext is Activity) {
                                            mContext.finish()
                                            mContext.overridePendingTransition(R.anim.right_to_current,R.anim.current_to_left )
                                        }
                                        disposable?.dispose()
                                    }
                                }
                            },{t-> Log.e(ErrorManager.DELETE_USER,t.message.toString())})
                    } else {
                        Toast.makeText(mContext, "탈퇴가 제대로 진행되지않았습니다.\n" + "다시 시도해주세요."
                            , Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                })?.addOnFailureListener {
                    Toast.makeText(mContext, "탈퇴가 제대로 진행되지않았습니다.\n" +
                                "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    Log.e("구글삭제 실패", it.message.toString())
                    dialog.dismiss()
                }
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }
}