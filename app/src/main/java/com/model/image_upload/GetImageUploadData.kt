package com.model.image_upload

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetImageUploadData {
    @SerializedName("data")
    @Expose
    var data: Data? = null
}