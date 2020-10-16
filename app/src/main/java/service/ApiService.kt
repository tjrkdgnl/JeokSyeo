package service

import io.reactivex.Single
import model.Data
import model.MyStatus
import model.TokenData
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("v1/jeokSyeo/getAlcholListByMostLike")
    fun getAlcholListByMostLike(@FieldMap field :HashMap<String,Any>) : Single<MyStatus>

    @POST("v1/auth/signup")
    fun signUp(@Header("X-Request-ID") UUID :String ,@Body map :HashMap<String,Any> ) : Single<TokenData>

    @POST("v1/auth/refresh")
    fun refreshToken(@Header("X-Request-ID") UUID: String, @Body map :HashMap<String,Any>) :Single<TokenData>


}