package com.model.user

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class User {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("nickname")
    @Expose
    var nickname: String? = null

    @SerializedName("profile")
    @Expose
    var profile: List<Profile>? = null

    @SerializedName("role")
    @Expose
    var role: String? = null

    @SerializedName("level")
    @Expose
    var level: Int? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("birth")
    @Expose
    var birth: String? = null
}
