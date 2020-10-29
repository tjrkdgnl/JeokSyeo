package com.model.result

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("result")
    @Expose
    var result: String? = null
}