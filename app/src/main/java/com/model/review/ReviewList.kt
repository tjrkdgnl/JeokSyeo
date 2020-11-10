package com.model.review

import com.application.GlobalApplication
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.Medium

class ReviewList {
    @SerializedName("review_id")
    @Expose
    var review_id: String? = null

    @SerializedName("nickname")
    @Expose
    var nickname: String? = null

    @SerializedName("level")
    @Expose
    var level: Int? = null

    @SerializedName("contents")
    @Expose
    var contents: String? = null

    @SerializedName("score")
    @Expose
    var score: Double? = null

    @SerializedName("like_count")
    @Expose
    var likeCount: Int? = null

    @SerializedName("has_like")
    @Expose
    var has_like: Boolean? = null

    @SerializedName("dislike_count")
    @Expose
    var dislikeCount: Int? = null

    @SerializedName("has_dislike")
    @Expose
    var has_dislike: Boolean? = null

    @SerializedName("profile")
    @Expose
    var profile: List<Medium>? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Int? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Int? = null

    var checkMore =GlobalApplication.DETAIL_REVIEW
}