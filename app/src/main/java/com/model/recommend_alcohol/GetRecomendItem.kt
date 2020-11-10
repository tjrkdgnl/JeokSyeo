package com.model.recommend_alcohol

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetRecomendItem {
    @SerializedName("data")
    @Expose
    var data: RecommendData? = null
}