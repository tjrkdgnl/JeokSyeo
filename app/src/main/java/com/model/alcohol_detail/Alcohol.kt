package com.model.alcohol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.model.common.Brewery
import com.model.common.MainClass
import com.model.common.Medium
import com.model.common.Name


class Alcohol() : Parcelable {
    @SerializedName("alcohol_id")
    @Expose
    var alcoholId: String? = null

    @SerializedName("name")
    @Expose
    var name: Name? = null

    @SerializedName("media")
    @Expose
    var media: List<Medium?>? = null

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

    @SerializedName("like_count")
    @Expose
    var likeCount: Int? = null

    @SerializedName("view_count")
    @Expose
    var viewCount: Int? = null

    @SerializedName("abv")
    @Expose
    var abv: String? = null

    @SerializedName("adjunct")
    @Expose
    var adjunct: List<String>? = null

    @SerializedName("barrel_aged")
    @Expose
    var barrelAged: Boolean? = null

    @SerializedName("production_year")
    @Expose
    var productionYear: Int? = null

    @SerializedName("sale")
    @Expose
    var sale: Boolean? = null

    @SerializedName("capacity")
    @Expose
    var capacity: Int? = null

    @SerializedName("food_pairing")
    @Expose
    var foodPairing: List<String>? = null

    constructor(parcel: Parcel) : this() {
        alcoholId = parcel.readString()
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
        likeCount = parcel.readValue(Int::class.java.classLoader) as? Int
        viewCount = parcel.readValue(Int::class.java.classLoader) as? Int
        abv = parcel.readString()
        adjunct = parcel.createStringArrayList()
        barrelAged = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        productionYear = parcel.readValue(Int::class.java.classLoader) as? Int
        sale = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        capacity = parcel.readValue(Int::class.java.classLoader) as? Int
        foodPairing = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(alcoholId)
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
        parcel.writeValue(likeCount)
        parcel.writeValue(viewCount)
        parcel.writeString(abv)
        parcel.writeStringList(adjunct)
        parcel.writeValue(barrelAged)
        parcel.writeValue(productionYear)
        parcel.writeValue(sale)
        parcel.writeValue(capacity)
        parcel.writeStringList(foodPairing)
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