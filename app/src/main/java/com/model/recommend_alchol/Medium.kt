package com.model.recommend_alchol

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Medium {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("media_id")
    @Expose
    var mediaId: String? = null

    @SerializedName("media_resource")
    @Expose
    var mediaResource: MediaResource? = null
}