package com.model.alcohol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class More() :Parcelable {
    @SerializedName("hop")
    @Expose
    var hop: List<String>? = null

    @SerializedName("aged_year")
    @Expose
    var aged_year: String? = null

    @SerializedName("ibu")
    @Expose
    var ibu: Float? = null

    @SerializedName("srm")
    @Expose
    var srm: Srm? = null

    @SerializedName("color")
    @Expose
    var color: Color? = null

    @SerializedName("temperature")
    @Expose //
    var temperature: List<String>? = null

    @SerializedName("filtered")
    @Expose
    var filtered: Boolean? = null

    @SerializedName("rpr")
    @Expose
    var rpr: Float? = null

    @SerializedName("smv")
    @Expose
    var smv: Float? = null

    @SerializedName("sake_type")
    @Expose
    var sake_type: String? = null

    @SerializedName("body")
    @Expose
    var body: String? = null

    @SerializedName("acidity")
    @Expose
    var acidity: String? = null

    @SerializedName("tannin")
    @Expose
    var tannin: String? = null

    @SerializedName("sweet")
    @Expose
    var sweet: String? = null

    @SerializedName("grape")
    @Expose
    var grape: List<String>? = null

    @SerializedName("malt")
    @Expose
    var malt: List<String>? = null

    @SerializedName("cask_type")
    @Expose
    var cask_type: String? = null

    constructor(parcel: Parcel) : this() {
        hop = parcel.createStringArrayList()
        aged_year = parcel.readString()
        ibu = parcel.readValue(Float::class.java.classLoader) as? Float
        srm = parcel.readParcelable(Srm::class.java.classLoader)
        color = parcel.readParcelable(Color::class.java.classLoader)
        temperature = parcel.createStringArrayList()
        filtered = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        rpr = parcel.readValue(Float::class.java.classLoader) as? Float
        smv = parcel.readValue(Float::class.java.classLoader) as? Float
        sake_type = parcel.readString()
        body = parcel.readString()
        acidity = parcel.readString()
        tannin = parcel.readString()
        sweet = parcel.readString()
        grape = parcel.createStringArrayList()
        malt = parcel.createStringArrayList()
        cask_type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(hop)
        parcel.writeString(aged_year)
        parcel.writeValue(ibu)
        parcel.writeParcelable(srm, flags)
        parcel.writeParcelable(color, flags)
        parcel.writeStringList(temperature)
        parcel.writeValue(filtered)
        parcel.writeValue(rpr)
        parcel.writeValue(smv)
        parcel.writeString(sake_type)
        parcel.writeString(body)
        parcel.writeString(acidity)
        parcel.writeString(tannin)
        parcel.writeString(sweet)
        parcel.writeStringList(grape)
        parcel.writeStringList(malt)
        parcel.writeString(cask_type)
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