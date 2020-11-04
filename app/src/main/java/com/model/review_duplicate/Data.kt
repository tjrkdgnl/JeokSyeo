package com.model.review_duplicate

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("isExist")
    @Expose
    var isExist: Boolean? = null
}