package com.model.alchol_ranking

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetAlcholRanking {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}