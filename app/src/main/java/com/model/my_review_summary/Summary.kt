package com.model.my_review_summary

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Summary {
    @SerializedName("review_count")
    @Expose
    var reviewCount: Int? = null

    @SerializedName("level")
    @Expose
    var level: Int? = null

    @SerializedName("nickname")
    @Expose
    var nickname: String? = null
}