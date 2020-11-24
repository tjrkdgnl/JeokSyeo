package com.model.alcohol_ranking

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ReviewInfo{
    @SerializedName("review_id")
    @Expose
    var reviewId: String? = null

    @SerializedName("contents")
    @Expose
    var contents: String? = null

}