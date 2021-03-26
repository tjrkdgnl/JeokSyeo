package com.service

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 서버 측에서 ios와 android를 구분하기 위해서 user-agent 값을 붙여서 전송하도록한다.
 */
class UserAgentInterceptor(val userAgent:String) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent =originalRequest.newBuilder()
            .removeHeader("User-Agent")
            .addHeader("User-Agent",userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}