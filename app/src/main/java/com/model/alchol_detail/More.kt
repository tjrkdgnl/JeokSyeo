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

    @SerializedName("body")
    @Expose
    var body: String? = null

    @SerializedName("acidic")
    @Expose
    var acidic: String? = null

    @SerializedName("tannin")
    @Expose
    var tannin: String? = null

    @SerializedName("sweet")
    @Expose
    var sweet: String? = null

    @SerializedName("polishing")
    @Expose
    var polishing: String? = null

    @SerializedName("filtered")
    @Expose
    var filtered: Boolean? = null

    @SerializedName("cask_type")
    @Expose
    var cask_type: Boolean? = null

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