package com.service

import com.model.alchol_category.GetAlcholCategory
import com.model.alchol_detail.GetAlcholDetail
import com.model.alchol_ranking.GetAlcholRanking
import com.model.area.GetAreaData
import com.model.banner.GetBannerData
import com.model.nickname_check.GetResult
import com.model.recommend_alchol.GetRecomendItem
import com.model.review.GetReviewData
import com.model.review_duplicate.GetReviewDuplicate
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
    fun getArea(@Header("X-Request-ID")UUID: String,@Query("c")code:String? ) : Flowable<GetAreaData>

    @GET("v1/auth/check-nickname")
    fun checkNickName(@Header("X-Request-ID")UUID: String,@Query("n")name:String ) : Single<GetResult>

    @GET("/v1/main/recommend")
    fun getRecommendAlchol(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) :Flowable<GetRecomendItem>

    @GET("v1/main/rank")
    fun getAlcholRanking(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) : Flowable<GetAlcholRanking>

    @GET("v1/alchols")
    fun getAlcholCategory(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
    @Query("f") type:String, @Query("c")number:Int, @Query("s")sort:String, @Query("p") pageNumber:String?) : Flowable<GetAlcholCategory>

    @GET("v1/alchols/{alchol_id}")
    fun getAlcholDetail(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                          @Path("alchol_id") alcholId:String) : Flowable<GetAlcholDetail>

    @POST("v1/alchols/{alchol_id}/like")
    fun alcholLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                        @Path("alchol_id") alcholId:String?) : Flowable<GetAlcholDetail>

    @DELETE("v1/alchols/{alchol_id}/like")
    fun cancelAlcholLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                   @Path("alchol_id") alcholId:String?) : Flowable<com.model.result.GetResult>

    @POST("v1/alchols/{alchol_id}/reviews")
    fun setComment(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                   @Path("alchol_id") alcholId:String?,@Body map:HashMap<String,Any>) : Single<com.model.result.GetResult>

    @GET("v1/alchols/{alchol_id}/reviews")
    fun getAlcholReivew(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                        @Path("alchol_id") alcholId:String) : Flowable<GetReviewData>

    @GET("v1/main/banner")
    fun getBannerData(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) :Flowable<GetBannerData>

    @GET("v1/alchols/{alchol_id}/reviews/check")
    fun checkReviewDuplicate(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                      @Path("alchol_id")alcholId: String) :Single<GetReviewDuplicate>

    @POST("v1/alchols/{alchol_id}/reviews/{review_id}/like")
    fun setLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                @Path("alchol_id")alcholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @DELETE("v1/alchols/{alchol_id}/reviews/{review_id}/like")
    fun setUnLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Path("alchol_id")alcholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @POST("v1/alchols/{alchol_id}/reviews/{review_id}/dislike")
    fun setDislike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Path("alchol_id")alcholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @DELETE("v1/alchols/{alchol_id}/reviews/{review_id}/dislike")
    fun setUnDislike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Path("alchol_id")alcholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>
}