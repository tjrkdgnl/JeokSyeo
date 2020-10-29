package com.model.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Brewery() :Parcelable {
    @SerializedName("brewery_id")
    @Expose
    var breweryId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    constructor(parcel: Parcel) : this() {
        breweryId = parcel.readString()
        name = parcel.readString()
        location = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(breweryId)
        parcel.writeString(name)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Brewery> {
        override fun createFromParcel(parcel: Parcel): Brewery {
            return Brewery(parcel)
        }

        override fun newArray(size: Int): Array<Brewery?> {
            return arrayOfNulls(size)
        }
    }
}