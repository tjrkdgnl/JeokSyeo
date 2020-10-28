package com.model.common

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class FirstClass {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}