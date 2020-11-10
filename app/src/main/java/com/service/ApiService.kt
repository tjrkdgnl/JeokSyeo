package com.service

import com.model.alcohol_category.GetAlcoholCategory
import com.model.alcohol_detail.GetAlcoholDetail
import com.model.alcohol_ranking.GetAlcoholRanking
import com.model.area.GetAreaData
import com.model.banner.GetBannerData
import com.model.image_upload.GetImageUploadData
import com.model.my_comment.GetCommentData
import com.model.my_review_summary.GetMyReviewSum
import com.model.rated.GetRatedList
import com.model.nickname_check.GetResult
import com.model.recommend_alcohol.GetRecomendItem
import com.model.review.GetReviewData
import com.model.review_duplicate.GetReviewDuplicate
import io.reactivex.Single
import com.model.token.GetUserData
import com.model.user.GetUserInfo
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.File

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
    fun getRecommendAlcohol(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) :Flowable<GetRecomendItem>

    @GET("v1/main/rank")
    fun getAlcoholRanking(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) : Flowable<GetAlcoholRanking>

    @GET("v1/alcohols")
    fun getAlcoholCategory(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
    @Query("f") type:String, @Query("c")number:Int, @Query("s")sort:String, @Query("p") pageNumber:String?) : Flowable<GetAlcoholCategory>

    @GET("v1/alcohols/{alcohol_id}")
    fun getAlcoholDetail(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                          @Path("alcohol_id") alcoholId:String) : Flowable<GetAlcoholDetail>

    @POST("v1/alcohols/{alcohol_id}/like")
    fun alcoholLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                        @Path("alcohol_id") alcoholId:String?) : Flowable<GetAlcoholDetail>

    @DELETE("v1/alcohols/{alcohol_id}/like")
    fun cancelAlcoholLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                   @Path("alcohol_id") alcoholId:String?) : Flowable<com.model.result.GetResult>

    @POST("v1/alcohols/{alcohol_id}/reviews")
    fun setComment(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                   @Path("alcohol_id") alcoholId:String?,@Body map:HashMap<String,Any>) : Single<com.model.result.GetResult>

    @GET("v1/alcohols/{alcohol_id}/reviews")
    fun getAlcoholReivew(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                        @Path("alcohol_id") alcoholId:String?,@Query("p")page:Int) : Flowable<GetReviewData>

    @GET("v1/main/banner")
    fun getBannerData(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) :Flowable<GetBannerData>

    @GET("v1/alcohols/{alcohol_id}/reviews/check")
    fun checkReviewDuplicate(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                      @Path("alcohol_id")alcoholId: String) :Single<GetReviewDuplicate>

    @POST("v1/alcohols/{alcohol_id}/reviews/{review_id}/like")
    fun setLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                @Path("alcohol_id")alcoholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @DELETE("v1/alcohols/{alcohol_id}/reviews/{review_id}/like")
    fun setUnLike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Path("alcohol_id")alcoholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @POST("v1/alcohols/{alcohol_id}/reviews/{review_id}/dislike")
    fun setDislike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Path("alcohol_id")alcoholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @DELETE("v1/alcohols/{alcohol_id}/reviews/{review_id}/dislike")
    fun setUnDislike(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Path("alcohol_id")alcoholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @GET("v1/users/reviews")
    fun getMyRatedList(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                       @Query("f")alcoholType:String?,@Query("c")alcoholCount:Int,@Query("p")pageNumber:Int) :Flowable<GetRatedList>


    @GET("v1/users/reviews/summary")
    fun getMyRatedReviewSum(@Header("X-Request-ID")UUID: String, @Header("Authorization")token: String?) :Single<GetMyReviewSum>

    @DELETE("v1/alcohols/{alcohol_id}/reviews/{review_id}")
    fun deleteMyRatedReview(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                  @Path("alcohol_id")alcoholId: String?,@Path("review_id")reviewId:String?):Single<com.model.result.GetResult>

    @PUT("v1/alcohols/{alcohol_id}/reviews/{review_id}")
    fun editMyRatedReview(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                          @Path("alcohol_id") alcoholId:String?,@Path("review_id")reviewId:String?
                          ,@Body map:HashMap<String,Any>) : Flowable<com.model.result.GetResult>

    @DELETE("v1/users/close")
    fun deleteUser(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?) :Flowable<com.model.result.GetResult>

    @Multipart
    @POST("v1/upload/image")
    fun imageUpload(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Part imageBody :MultipartBody.Part?) :Single<GetImageUploadData>

    @PUT("v1/users")
    fun editProfile(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                    @Body map:HashMap<String,Any>):Single<com.model.result.GetResult>

    @GET("v1/users")
    fun getUserInfo(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?):Single<GetUserInfo>

    @GET("v1/alcohols/{alcohol_id}/reviews/{review_id}")
    fun getCommentOfAlcohol(@Header("X-Request-ID")UUID: String,@Header("Authorization")token: String?,
                           @Path("alcohol_id") alcoholId: String?,@Path("review_id") reviewId:String?):Single<GetCommentData>
}