package com.model.favorite

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.MediaResource


class Summary {
    @SerializedName("alcohol_like_count")
    @Expose
    var alcoholLikeCount: Int? = null

    @SerializedName("nickname")
    @Expose
    var nickname: String? = null

    @SerializedName("profile")
    @Expose
    var profile: List<MediaResource>? = null

}