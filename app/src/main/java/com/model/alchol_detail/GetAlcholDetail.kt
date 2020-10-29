package com.model.alchol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetAlcholDetail() :Parcelable {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    constructor(parcel: Parcel) : this() {
        data = parcel.readParcelable(Data::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetAlcholDetail> {
        override fun createFromParcel(parcel: Parcel): GetAlcholDetail {
            return GetAlcholDetail(parcel)
        }

        override fun newArray(size: Int): Array<GetAlcholDetail?> {
            return arrayOfNulls(size)
        }
    }


}