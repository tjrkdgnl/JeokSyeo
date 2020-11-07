package com.model.user

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("userInfo")
    @Expose
    var userInfo: User? = null
}