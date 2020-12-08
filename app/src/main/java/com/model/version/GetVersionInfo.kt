package com.model.version

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetVersionInfo {
    @SerializedName("data")
    @Expose
    var data: Data? = null

}