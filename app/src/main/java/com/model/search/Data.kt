package com.model.search

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("alcoholList")
    @Expose
    var alcoholList: List<String>? = null

    var type =0

}