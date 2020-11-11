package com.model.level

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("level")
    @Expose
    var level: Int = 0

    @SerializedName("level_5_rank")
    @Expose
    var level5Rank: Int = 0

    @SerializedName("review_count")
    @Expose
    var reviewCount: Int = 0

}