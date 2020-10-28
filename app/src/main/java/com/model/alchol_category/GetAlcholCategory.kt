package com.model.alchol_category

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetAlcholCategory {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}