package com.model.version

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("platform")
    @Expose
    var platform: String? = null

    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("change_log")
    @Expose
    var changeLog: String? = null

}