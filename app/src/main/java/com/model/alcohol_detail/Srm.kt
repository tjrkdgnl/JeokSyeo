package com.model.alcohol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Srm() : Parcelable {
    @SerializedName("srm")
    @Expose
    var srm: Double? = null

    @SerializedName("rgb_hex")
    @Expose
    var rgbHex: String? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    constructor(parcel: Parcel) : this() {
        srm = parcel.readValue(Double::class.java.classLoader) as? Double
        rgbHex = parcel.readString()
        color = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(srm)
        parcel.writeString(rgbHex)
        parcel.writeString(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Srm> {
        override fun createFromParcel(parcel: Parcel): Srm {
            return Srm(parcel)
        }

        override fun newArray(size: Int): Array<Srm?> {
            return arrayOfNulls(size)
        }
    }

}
