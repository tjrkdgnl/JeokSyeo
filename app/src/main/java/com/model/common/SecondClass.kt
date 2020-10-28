package com.model.common

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class SecondClass {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}