package com.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.PageInfo

class Data {
    @SerializedName("pageInfo")
    @Expose
    var pageInfo: PageInfo? = null

    @SerializedName("reviewInfo")
    @Expose
    var reviewInfo: ReviewInfo? = null

    @SerializedName("reviewList")
    @Expose
    var reviewList: List<ReviewList>? = null
}