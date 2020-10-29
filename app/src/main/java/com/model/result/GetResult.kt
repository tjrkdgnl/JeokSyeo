package com.model.result

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetResult {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}