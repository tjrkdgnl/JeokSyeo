package com.model.rated

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ReviewList {
    @SerializedName("review_id")
    @Expose
    var reviewId: String? = null

    @SerializedName("alcohol")
    @Expose
    var alcohol: Alcohol? = null

    @SerializedName("contents")
    @Expose
    var contents: String? = null

    @SerializedName("score")
    @Expose
    var score: Double? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Int? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Int? = null
}