package com.model.recommend_alchol

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class MediaResource {
    @SerializedName("small")
    @Expose
    var small: Small? = null

    @SerializedName("medium")
    @Expose
    var medium: Medium_? = null

    @SerializedName("large")
    @Expose
    var large: Large? = null
}