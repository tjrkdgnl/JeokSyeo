package com.model.token

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetUserData {
    @SerializedName("data")
    @Expose
    var data: Data? = null

}