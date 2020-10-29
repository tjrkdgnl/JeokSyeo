package com.model.alchol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class BackgroundMedia() :Parcelable {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("media_id")
    @Expose
    var mediaId: String? = null

    @SerializedName("media_resource")
    @Expose
    var mediaResource: BackmediaResource? = null

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
        mediaId = parcel.readString()
        mediaResource = parcel.readParcelable(BackmediaResource::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(mediaId)
        parcel.writeParcelable(mediaResource, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BackgroundMedia> {
        override fun createFromParcel(parcel: Parcel): BackgroundMedia {
            return BackgroundMedia(parcel)
        }

        override fun newArray(size: Int): Array<BackgroundMedia?> {
            return arrayOfNulls(size)
        }
    }


}