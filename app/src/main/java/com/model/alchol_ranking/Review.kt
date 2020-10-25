package com.model.alchol_ranking

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Review {
    @SerializedName("score")
    @Expose
    var score: Int? = null

    @SerializedName("reviews")
    @Expose
    var reviews: List<Any>? = null
}