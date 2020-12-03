package com.model.user.profileToPojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProfileInfo {
    @SerializedName("profile")
    @Expose
    var profile: Profile? = null

    @SerializedName("nickname")
    @Expose
    var nickname: String? = null

    @SerializedName("birth")
    @Expose
    var birth: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null
}