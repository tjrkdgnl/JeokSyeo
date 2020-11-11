package com.model.alcohol_category

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.Brewery
import com.model.common.MainClass
import com.model.common.Medium


class AlcoholList {
    @SerializedName("alcohol_id")
    @Expose
    var alcoholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium>? = null

    @SerializedName("class")
    @Expose
    var class_: MainClass? = null

    @SerializedName("brewery")
    @Expose
    var brewery: List<Brewery>? = null

    @SerializedName("like_count")
    @Expose
    var likeCount: Int? = null

    @SerializedName("view_count")
    @Expose
    var viewCount: Int? = null

    @SerializedName("review")
    @Expose
    var review: Review? = null

    @SerializedName("isLiked")
    @Expose
    var isLiked: Boolean? = null

    @SerializedName("abv")
    @Expose
    var abv: String? = null
    get() = "$field%"
}