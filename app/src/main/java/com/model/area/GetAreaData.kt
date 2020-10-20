package com.model.area
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class GetAreaData {
    @SerializedName("data")
    @Expose
    var data: Area? = null
}