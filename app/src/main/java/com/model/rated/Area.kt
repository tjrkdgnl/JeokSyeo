package com.model.rated

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Area {
    @SerializedName("kr")
    @Expose
    var kr: String? = null

    @SerializedName("en")
    @Expose
    var en: String? = null
}