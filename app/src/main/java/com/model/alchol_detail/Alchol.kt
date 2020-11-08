package com.model.alchol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.Brewery
import com.model.common.MainClass
import com.model.common.Medium
import com.model.common.Name


class Alchol() :Parcelable {
    @SerializedName("alchol_id")
    @Expose
    var alcholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium>? = null

    @SerializedName("background_media")
    @Expose
    var backgroundMedia: List<BackgroundMedia>? = null

    @SerializedName("class")
    @Expose
    var class_: MainClass? = null

    @SerializedName("brewery")
    @Expose
    var brewery: List<Brewery>? = null

    @SerializedName("container")
    @Expose
    var container: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("more")
    @Expose
    var more: More? = null

    @SerializedName("award")
    @Expose
    var award: List<String>? = null

    @SerializedName("isLiked")
    @Expose
    var isLiked: Boolean? = null

    @SerializedName("abv")
    @Expose
    var abv: String? = null

    @SerializedName("adjunct")
    @Expose
    var adjunct: String? = null

    @SerializedName("temperature")
    @Expose
    var temperature: String? = null

    @SerializedName("barrel_aged")
    @Expose
    var barrelAged: Boolean? = null

    @SerializedName("production_year")
    @Expose
    var productionYear: String? = null

    @SerializedName("sale")
    @Expose
    var sale: Boolean? = null

    @SerializedName("capacity")
    @Expose
    var capacity: String? = null

    @SerializedName("food_pairing")
    @Expose
    var foodPairing: String? = null

    @SerializedName("review")
    @Expose
    var review: Review? = null

    constructor(parcel: Parcel) : this() {
        alcholId = parcel.readString()
        name = parcel.readParcelable(Name::class.java.classLoader)
        media = parcel.createTypedArrayList(Medium)
        backgroundMedia = parcel.createTypedArrayList(BackgroundMedia)
        class_ = parcel.readParcelable(MainClass::class.java.classLoader)
        brewery = parcel.createTypedArrayList(Brewery)
        container = parcel.readString()
        description = parcel.readString()
        more = parcel.readParcelable(More::class.java.classLoader)
        award = parcel.createStringArrayList()
        isLiked = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        abv = parcel.readString()
        adjunct = parcel.readString()
        temperature = parcel.readString()
        barrelAged = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        productionYear = parcel.readString()
        sale = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        capacity = parcel.readString()
        foodPairing = parcel.readString()
        review = parcel.readParcelable(Review::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(alcholId)
        parcel.writeParcelable(name, flags)
        parcel.writeTypedList(media)
        parcel.writeTypedList(backgroundMedia)
        parcel.writeParcelable(class_, flags)
        parcel.writeTypedList(brewery)
        parcel.writeString(container)
        parcel.writeString(description)
        parcel.writeParcelable(more, flags)
        parcel.writeStringList(award)
        parcel.writeValue(isLiked)
        parcel.writeString(abv)
        parcel.writeString(adjunct)
        parcel.writeString(temperature)
        parcel.writeValue(barrelAged)
        parcel.writeString(productionYear)
        parcel.writeValue(sale)
        parcel.writeString(capacity)
        parcel.writeString(foodPairing)
        parcel.writeParcelable(review, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alchol> {
        override fun createFromParcel(parcel: Parcel): Alchol {
            return Alchol(parcel)
        }

        override fun newArray(size: Int): Array<Alchol?> {
            return arrayOfNulls(size)
        }
    }
}