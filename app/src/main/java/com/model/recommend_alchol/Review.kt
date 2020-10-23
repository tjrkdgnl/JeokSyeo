package com.model.recommend_alchol

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Review {
    @SerializedName("score")
    @Expose
    var score: Int? = null
}