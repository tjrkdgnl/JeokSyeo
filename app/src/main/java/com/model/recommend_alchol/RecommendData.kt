package com.model.recommend_alchol

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RecommendData {
    @SerializedName("alcholList")
    @Expose
    var alcholList: List<AlcholList>? = null
}