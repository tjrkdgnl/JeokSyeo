package com.jeoksyeo.wet.activity.login.google

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.vuforia.engine.wet.R
import com.error.ErrorManager
import com.google.firebase.auth.FirebaseAuth

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

        Log.e("구글로그아웃",FirebaseAuth.getInstance().currentUser?.email.toString())
        FirebaseAuth.getInstance().signOut()
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