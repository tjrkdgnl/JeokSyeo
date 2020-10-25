package com.model.alchol_ranking

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Brewery {
    @SerializedName("brewery_id")
    @Expose
    var breweryId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null
}