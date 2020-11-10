package com.model.alcohol_ranking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("alcoholList")
    @Expose
    var alcoholList: List<AlcoholList>? = null
}