package com.model.alcohol_category

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetAlcoholCategory {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}