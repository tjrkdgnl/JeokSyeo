package model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("token")
    @Expose
    var token: Token? = null

}