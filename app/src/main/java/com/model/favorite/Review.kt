package com.model.favorite

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Review {
    @SerializedName("score")
    @Expose
    var score: Float? = null

}