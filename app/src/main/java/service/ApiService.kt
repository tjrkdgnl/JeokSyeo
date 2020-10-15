package service

import io.reactivex.Single
import model.MyStatus
import model.Token
import org.json.JSONObject
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("v1/jeokSyeo/getAlcholListByMostLike")
    fun getAlcholListByMostLike(@FieldMap field :HashMap<String,Any>) : Single<MyStatus>


    @POST("v1/auth/signup")
    fun signUp(@Header("X-Request-ID") UUID :String ,@Body map :HashMap<String,Any> ) : Single<Token>

}