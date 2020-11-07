package com.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.MediaResource


class Profile {
    @SerializedName("media_resource")
    @Expose
    var mediaResource: MediaResource? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("media_id")
    @Expose
    var mediaId: String? = null
}