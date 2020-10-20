package com.model.area

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Area {
    @SerializedName("areaList")
    @Expose
    var areaList: List<AreaList>? = null
}