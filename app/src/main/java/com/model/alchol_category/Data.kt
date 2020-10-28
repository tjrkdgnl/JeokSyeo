package com.model.alchol_category

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("pagingInfo")
    @Expose
    var pagingInfo: PagingInfo? = null

    @SerializedName("alcholList")
    @Expose
    var alcholList: List<AlcholList>? = null
}