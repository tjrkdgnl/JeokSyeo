package com.service

import android.webkit.WebView
import androidx.databinding.library.BuildConfig
import com.application.GlobalApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiGenerator {
    //"http://192.168.1.3:9090/"    //로컬서버
    //http://dev.jeoksyeo.com/     //개발서버
    //"https://api.jeoksyeo.com/" //운영서버

    private const val BASE_URL =  "http://dev.jeoksyeo.com/"


    val retrofit :Retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = when {
                BuildConfig.DEBUG ->HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
        } as Interceptor)
            .addInterceptor(UserAgentInterceptor(WebView(GlobalApplication.instance).settings.userAgentString)).build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
}