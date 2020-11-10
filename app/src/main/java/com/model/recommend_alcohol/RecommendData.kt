package com.model.recommend_alcohol

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RecommendData {
    @SerializedName("alcoholList")
    @Expose
    var alcoholList: List<AlcoholList>? = null
}