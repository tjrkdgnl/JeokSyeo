package com.model.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class FirstClass() :Parcelable {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        code = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirstClass> {
        override fun createFromParcel(parcel: Parcel): FirstClass {
            return FirstClass(parcel)
        }

        override fun newArray(size: Int): Array<FirstClass?> {
            return arrayOfNulls(size)
        }
    }
}