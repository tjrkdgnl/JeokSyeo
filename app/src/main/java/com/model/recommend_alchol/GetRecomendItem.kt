package com.model.recommend_alchol

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetRecomendItem {
    @SerializedName("data")
    @Expose
    var data: RecommendData? = null
}