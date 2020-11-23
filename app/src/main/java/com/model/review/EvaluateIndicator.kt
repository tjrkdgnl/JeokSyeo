package com.model.review

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class EvaluateIndicator {
    @SerializedName("score")
    @Expose
    var score: Double? = null

    @SerializedName("aroma")
    @Expose
    var aroma: Double? = null

    @SerializedName("mouthfeel")
    @Expose
    var mouthfeel: Double? = null

    @SerializedName("taste")
    @Expose
    var taste: Double? = null

    @SerializedName("appearance")
    @Expose
    var appearance: Double? = null

    @SerializedName("overall")
    @Expose
    var overall: Double? = null

}