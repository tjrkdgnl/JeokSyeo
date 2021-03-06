package com.model.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Medium() :Parcelable {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("media_id")
    @Expose
    var mediaId: String? = null

    @SerializedName("media_resource")
    @Expose
    var mediaResource: MediaResource? = null

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
        mediaId = parcel.readString()
        mediaResource = parcel.readParcelable(MediaResource::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(mediaId)
        parcel.writeParcelable(mediaResource, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Medium> {
        override fun createFromParcel(parcel: Parcel): Medium {
            return Medium(parcel)
        }

        override fun newArray(size: Int): Array<Medium?> {
            return arrayOfNulls(size)
        }
    }
}