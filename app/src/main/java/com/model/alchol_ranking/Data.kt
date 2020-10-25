package com.model.alchol_ranking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("alcholList")
    @Expose
    var alcholList: List<AlcholList>? = null
}