package com.model.image_upload

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.MediaResource


class Data {
    @SerializedName("media_id")
    @Expose
    var mediaId: String? = null

    @SerializedName("media_resource")
    @Expose
    var mediaResource: MediaResource? = null
}