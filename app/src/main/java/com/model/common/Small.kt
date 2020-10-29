package com.model.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Small() : Parcelable{
    @SerializedName("src")
    @Expose
    var src: String? = null

    constructor(parcel: Parcel) : this() {
        src = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(src)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Small> {
        override fun createFromParcel(parcel: Parcel): Small {
            return Small(parcel)
        }

        override fun newArray(size: Int): Array<Small?> {
            return arrayOfNulls(size)
        }
    }
}