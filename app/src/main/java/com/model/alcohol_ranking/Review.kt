package com.model.alcohol_ranking

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Review {
    @SerializedName("score")
    @Expose
    var score: Float? = null

    @SerializedName("reviews")
    @Expose
    var reviews: List<Any>? = null
}