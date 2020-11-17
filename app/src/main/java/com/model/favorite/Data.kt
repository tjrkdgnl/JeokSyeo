package com.model.favorite

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.PageInfo


class Data {
    @SerializedName("pagingInfo")
    @Expose
    var pagingInfo: PageInfo? = null

    @SerializedName("summary")
    @Expose
    var summary: Summary? = null

    @SerializedName("alcoholList")
    @Expose
    var alcoholList: List<AlcoholList>? = null

}