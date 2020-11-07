package com.model.user

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetUserInfo {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}