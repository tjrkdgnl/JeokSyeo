package com.service

import com.vuforia.engine.wet.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiGenerator {
    //"http://192.168.1.3:9090/"
    //http://dev.jeoksyeo.com/
    //"https://api.jeoksyeo.com/"

    private const val BASE_URL =  "http://dev.jeoksyeo.com/"

    val retrofit :Retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = when {
                BuildConfig.DEBUG ->HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
        } as Interceptor).build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
}