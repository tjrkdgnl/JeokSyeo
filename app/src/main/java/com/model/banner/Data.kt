package com.model.banner

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("banner")
    @Expose
    var banner: List<Banner>? = null
}