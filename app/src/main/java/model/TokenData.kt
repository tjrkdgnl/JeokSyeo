package model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class TokenData {
    @SerializedName("data")
    @Expose
    var data: Data? = null



}