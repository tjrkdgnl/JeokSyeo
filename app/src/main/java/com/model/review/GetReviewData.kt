package com.model.review

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetReviewData {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}