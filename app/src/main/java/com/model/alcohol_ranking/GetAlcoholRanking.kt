package com.model.alcohol_ranking

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetAlcoholRanking {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}