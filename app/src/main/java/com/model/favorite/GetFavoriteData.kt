package com.model.favorite

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetFavoriteData {
    @SerializedName("data")
    @Expose
    var data: Data? = null

}