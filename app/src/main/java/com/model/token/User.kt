package com.model.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("has_email")
    @Expose
    var hasEmail: Boolean? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("has_gender")
    @Expose
    var hasGender: Boolean? = null

    @SerializedName("nickname")
    @Expose
    var nickname: String? = null

    @SerializedName("has_nickname")
    @Expose
    var hasNickname: Boolean? = null

    @SerializedName("birth")
    @Expose
    var birth: String? = null

    @SerializedName("has_birth")
    @Expose
    var hasBirth: Boolean? = null
}