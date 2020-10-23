package com.model.recommend_alchol

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AlcholList {
    @SerializedName("alchol_id")
    @Expose
    var alcholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium>? = null

    @SerializedName("class")
    @Expose
    var class_: Class<*>? = null

    @SerializedName("review")
    @Expose
    var review: Review? = null

    @SerializedName("alchol_like_count")
    @Expose
    var alcholLikeCount: Int? = null

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