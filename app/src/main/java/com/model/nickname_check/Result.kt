package com.model.nickname_check

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Result {
    @SerializedName("result")
    @Expose
    var result: Boolean? = null
}