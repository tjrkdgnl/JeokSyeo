package com.model.nickname_check

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetResult {
    @SerializedName("data")
    @Expose
    var data: Result? = null
}