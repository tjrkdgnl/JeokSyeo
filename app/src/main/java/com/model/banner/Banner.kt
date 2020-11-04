package com.model.banner

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.Medium


class Banner {
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("media")
    @Expose
    var media: Medium? = null
}