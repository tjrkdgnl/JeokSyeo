package com.model.alchol_ranking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.Class
import com.model.common.Medium
import com.model.common.Name


class AlcholList {
    @SerializedName("alchol_id")
    @Expose
    var alcholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium>? = null

    @SerializedName("class")
    @Expose
    var class_: Class<*>? = null

    @SerializedName("brewery")
    @Expose
    var brewery: List<Brewery>? = null

    @SerializedName("review")
    @Expose
    var review: Review? = null
}