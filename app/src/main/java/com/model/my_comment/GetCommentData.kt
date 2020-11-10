package com.model.my_comment

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetCommentData() :Parcelable {
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

    companion object CREATOR : Parcelable.Creator<GetCommentData> {
        override fun createFromParcel(parcel: Parcel): GetCommentData {
            return GetCommentData(parcel)
        }

        override fun newArray(size: Int): Array<GetCommentData?> {
            return arrayOfNulls(size)
        }
    }
}