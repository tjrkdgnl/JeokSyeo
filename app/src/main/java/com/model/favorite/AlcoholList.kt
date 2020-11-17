package com.model.favorite

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.alcohol_detail.BackgroundMedia
import com.model.common.Brewery
import com.model.common.MainClass
import com.model.common.Medium
import com.model.common.Name


class AlcoholList {
    @SerializedName("alcohol_id")
    @Expose
    var alcoholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium>? = null

    @SerializedName("background_media")
    @Expose
    var backgroundMedia: List<BackgroundMedia>? = null

    @SerializedName("class")
    @Expose
    var class_: MainClass? = null

    @SerializedName("brewery")
    @Expose
    var brewery: List<Brewery>? = null

    @SerializedName("like_count")
    @Expose
    var like_count: Int? = null

    @SerializedName("view_count")
    @Expose
    var view_count: Int? = null

    @SerializedName("review")
    @Expose
    var review: Review? = null

    @SerializedName("abv")
    @Expose
    var abv: Float? = null


    var type =1

}