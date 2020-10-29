package com.model.alchol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class More() :Parcelable {
    @SerializedName("malt")
    @Expose
    var malt: String? = null

    @SerializedName("hop")
    @Expose
    var hop: String? = null

    @SerializedName("ibu")
    @Expose
    var ibu: String? = null

    @SerializedName("srm")
    @Expose
    var srm: String? = null

    @SerializedName("filtered")
    @Expose
    var filtered: Boolean? = null

    constructor(parcel: Parcel) : this() {
        malt = parcel.readString()
        hop = parcel.readString()
        ibu = parcel.readString()
        srm = parcel.readString()
        filtered = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(malt)
        parcel.writeString(hop)
        parcel.writeString(ibu)
        parcel.writeString(srm)
        parcel.writeValue(filtered)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<More> {
        override fun createFromParcel(parcel: Parcel): More {
            return More(parcel)
        }

        override fun newArray(size: Int): Array<More?> {
            return arrayOfNulls(size)
        }
    }


}