package com.model.user.profileToPojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Profile {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("media_id")
    @Expose
    var mediaId: String? = null
}