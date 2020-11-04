package com.model.banner

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetBannerData {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}