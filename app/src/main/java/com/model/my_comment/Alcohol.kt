package com.model.my_comment

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.model.common.Name


class Alcohol() :Parcelable {
    @SerializedName("alcohol_id")
    @Expose
    var alcoholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    constructor(parcel: Parcel) : this() {
        alcoholId = parcel.readString()
        name = parcel.readParcelable(Name::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(alcoholId)
        parcel.writeParcelable(name, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alcohol> {
        override fun createFromParcel(parcel: Parcel): Alcohol {
            return Alcohol(parcel)
        }

        override fun newArray(size: Int): Array<Alcohol?> {
            return arrayOfNulls(size)
        }
    }
}