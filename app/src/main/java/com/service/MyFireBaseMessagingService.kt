package com.service

import com.google.firebase.messaging.FirebaseMessagingService

class MyFireBaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}