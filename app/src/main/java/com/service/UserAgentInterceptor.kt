package com.service

import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor(val userAgent:String) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent =originalRequest.newBuilder()
            .removeHeader("User-Agent")
            .addHeader("Android",userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}