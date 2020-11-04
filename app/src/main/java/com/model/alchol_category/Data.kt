package com.model.alchol_category

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.PageInfo


class Data {
    @SerializedName("pagingInfo")
    @Expose
    var pagingInfo: PageInfo? = null

    @SerializedName("alcholList")
    @Expose
    var alcholList: List<AlcholList>? = null
}