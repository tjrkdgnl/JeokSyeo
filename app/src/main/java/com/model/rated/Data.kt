package com.model.rated

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.PageInfo


class Data {
    @SerializedName("pagingInfo")
    @Expose
    var pagingInfo: PageInfo? = null

    @SerializedName("reviewList")
    @Expose
    var reviewList: List<ReviewList>? = null
}