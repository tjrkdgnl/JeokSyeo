package com.model.common

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class PageInfo {
    @SerializedName("page")
    @Expose
    var page: String? = null

    @SerializedName("next")
    @Expose
    var next: Boolean? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null

    @SerializedName("alchol_total_count")
    @Expose
    var alcholTotalCount: Int? = null
}