package com.model.rated

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.Name

class Brewery {
    @SerializedName("_id")
    @Expose
    var _id: String? = null

    @SerializedName("detail")
    @Expose
    var detail: Detail? = null

    @SerializedName("schema_version")
    @Expose
    var schemaVersion: Int? = null

    @SerializedName("media")
    @Expose
    var media: List<Any>? = null

    @SerializedName("contacts")
    @Expose
    var contacts: List<Any>? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("brewery_id")
    @Expose
    var breweryId: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Int? = null
}