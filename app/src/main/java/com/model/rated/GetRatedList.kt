package com.model.rated

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRatedList {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}