package com.model.area

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class AreaList {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}