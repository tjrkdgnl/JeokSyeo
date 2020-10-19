package com.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Token {
    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null

    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String? = null

}