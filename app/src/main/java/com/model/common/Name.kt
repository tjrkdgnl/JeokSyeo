package com.model.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Name() : Parcelable {
    @SerializedName("kr")
    @Expose
    var kr: String? = null

    @SerializedName("en")
    @Expose
    var en: String? = null

    constructor(parcel: Parcel) : this() {
        kr = parcel.readString()
        en = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(kr)
        parcel.writeString(en)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Name> {
        override fun createFromParcel(parcel: Parcel): Name {
            return Name(parcel)
        }

        override fun newArray(size: Int): Array<Name?> {
            return arrayOfNulls(size)
        }
    }
}