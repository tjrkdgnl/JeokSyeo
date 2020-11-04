package com.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReviewInfo {
    @SerializedName("score_avg")
    @Expose
    var scoreAvg: Double? = null

    @SerializedName("score_1_count")
    @Expose
    var score1Count: Int? = null

    @SerializedName("score_2_count")
    @Expose
    var score2Count: Int? = null

    @SerializedName("score_3_count")
    @Expose
    var score3Count: Int? = null

    @SerializedName("score_4_count")
    @Expose
    var score4Count: Int? = null

    @SerializedName("score_5_count")
    @Expose
    var score5Count: Int? = null

    @SerializedName("review_total_count")
    @Expose
    var reviewTotalCount: Int? = null
}