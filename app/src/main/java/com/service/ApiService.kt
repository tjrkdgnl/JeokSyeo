package com.service

import com.model.area.GetAreaData
import io.reactivex.Single
import com.model.token.GetUserData
import io.reactivex.Flowable
import retrofit2.http.*

interface ApiService {

    @POST("v1/auth/token")
    fun signUp(@Header("X-Request-ID") UUID :String ,@Body map :HashMap<String,Any> ) : Single<GetUserData>

    @POST("v1/auth/token/refresh")
    fun refreshToken(@Header("X-Request-ID") UUID: String, @Body map :HashMap<String,Any>) :Single<GetUserData>

    @GET("v1/area")
    fun getArea(@Header("X-Request-ID")UUID: String,@Query("c")code:String ) : Flowable<GetAreaData>
}