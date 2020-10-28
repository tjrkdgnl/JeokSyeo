package com.model.common

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Small {
    @SerializedName("src")
    @Expose
    var src: String? = null
}