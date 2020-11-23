package com.service

import android.util.Log
import com.application.GlobalApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService

class MyFireBaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("디바이스 토큰 get",token)
    }
}