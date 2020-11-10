package com.model.alcohol_category

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Review {
    @SerializedName("score")
    @Expose
    var score: Float? = null

    @SerializedName("review_count")
    @Expose
    var reviewCount: Int? = null
}