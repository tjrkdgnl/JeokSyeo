package com.model.my_review_summary

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("summary")
    @Expose
    var summary: Summary? = null
}