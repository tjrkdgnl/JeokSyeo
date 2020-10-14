package service

import io.reactivex.Single
import model.MyStatus
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("jeokSyeo/getAlcholListByMostLike")
    fun getAlcholListByMostLike(@FieldMap field :HashMap<String,Any>) : Single<MyStatus>

}