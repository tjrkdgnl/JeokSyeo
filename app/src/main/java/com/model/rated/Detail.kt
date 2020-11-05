package com.model.rated

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Detail {
    @SerializedName("country")
    @Expose
    var country: Country? = null

    @SerializedName("area")
    @Expose
    var area: Area? = null
}