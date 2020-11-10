package com.model.alcohol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.Large
import com.model.common.Medium_
import com.model.common.Small

class BackmediaResource() :Parcelable {
    @SerializedName("small")
    @Expose
    var small: Small? = null

    @SerializedName("medium")
    @Expose
    var medium: Medium_? = null

    @SerializedName("large")
    @Expose
    var large: Large? = null

    constructor(parcel: Parcel) : this() {
        small = parcel.readParcelable(Small::class.java.classLoader)
        medium = parcel.readParcelable(Medium_::class.java.classLoader)
        large = parcel.readParcelable(Large::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(small, flags)
        parcel.writeParcelable(medium, flags)
        parcel.writeParcelable(large, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BackmediaResource> {
        override fun createFromParcel(parcel: Parcel): BackmediaResource {
            return BackmediaResource(parcel)
        }

        override fun newArray(size: Int): Array<BackmediaResource?> {
            return arrayOfNulls(size)
        }
    }


}