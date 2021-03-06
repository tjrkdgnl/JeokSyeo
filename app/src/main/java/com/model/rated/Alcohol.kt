package com.model.rated

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.Medium


class Alcohol {
    @SerializedName("alcohol_id")
    @Expose
    var alcoholId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium>? = null

    @SerializedName("brewery")
    @Expose
    var brewery: List<Brewery>? = null
}