package com.model.my_review_summary

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetMyReviewSum {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}