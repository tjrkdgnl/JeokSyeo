package com.model.recommend_alcohol

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.MainClass
import com.model.common.Medium
import com.model.common.Name


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

    @SerializedName("review")
    @Expose
    var review: Review? = null

    @SerializedName("alcohol_like_count")
    @Expose
    var alcoholLikeCount: Int? = null

    @SerializedName("isLiked")
    @Expose
    var isLiked: Boolean? = null

    @SerializedName("abv")
    @Expose
    var abv: String? = null

    @SerializedName("capacity")
    @Expose
    var capacity: String? = null
}