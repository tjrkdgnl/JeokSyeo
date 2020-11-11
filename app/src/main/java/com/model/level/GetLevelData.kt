package com.model.level

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetLevelData {
    @SerializedName("data")
    @Expose
    var data: Data? = null

}