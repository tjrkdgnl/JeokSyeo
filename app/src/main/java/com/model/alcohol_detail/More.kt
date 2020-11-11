package com.model.alcohol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class More() :Parcelable {
    @SerializedName("malt")
    @Expose
    var malt: List<String>? = null

    @SerializedName("hop")
    @Expose
    var hop: List<String>? = null

    @SerializedName("ibu")
    @Expose
    var ibu: Float? = null

    @SerializedName("srm")
    @Expose
    var srm: Srm? = null

    @SerializedName("temperature")
    @Expose
    var temperature: List<String>? = null

    @SerializedName("filtered")
    @Expose
    var filtered: Boolean? = null

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

    @SerializedName("cask")
    @Expose
    var cask: String? = null

    @SerializedName("sake_type")
    @Expose
    var sake_type: String? = null


    constructor(parcel: Parcel) : this() {
        malt = parcel.createStringArrayList()
        hop = parcel.createStringArrayList()
        ibu = parcel.readValue(Int::class.java.classLoader) as? Float
        srm = parcel.readParcelable(Srm::class.java.classLoader)
        filtered = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        temperature = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(malt)
        parcel.writeStringList(hop)
        parcel.writeValue(ibu)
        parcel.writeParcelable(srm, flags)
        parcel.writeValue(filtered)
        parcel.writeStringList(temperature)
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