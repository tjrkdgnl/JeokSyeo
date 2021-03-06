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

    @SerializedName("alcohol_total_count")
    @Expose
    var alcoholTotalCount: Int? = null
}