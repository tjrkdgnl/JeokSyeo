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

class GoogleLogin(private val mContext: Context,private val activity:Activity) {
    val gso:GoogleSignInOptions
    val instance:GoogleSignInClient

    init {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(mContext.getString(R.string.googleWebApplicationId))
            .requestEmail()
            .build()

         instance = GoogleSignIn.getClient(mContext,gso)
    }


    fun googleLogOut(){
        instance.signOut()
            .addOnCompleteListener(activity, OnCompleteListener { task ->
                Log.e(ErrorManager.Google_TAG,"로그아웃")
            })
    }

    fun googleDelete(){
        instance.revokeAccess()
            .addOnCompleteListener(activity, OnCompleteListener { task ->
                Log.e(ErrorManager.Google_TAG,"삭제")
            })
    }

}