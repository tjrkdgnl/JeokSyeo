package com.model.alcohol_detail

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Review() :Parcelable {
    @SerializedName("score")
    @Expose
    var score: Float? = null

    @SerializedName("aroma")
    @Expose
    var aroma: Double? = null

    @SerializedName("mouthfeel")
    @Expose
    var mouthfeel: Double? = null

    @SerializedName("taste")
    @Expose
    var taste: Double? = null

    @SerializedName("appearance")
    @Expose
    var appearance: Double? = null

    @SerializedName("overall")
    @Expose
    var overall: Double? = null

    @SerializedName("review_count")
    @Expose
    var reviewCount: Int? = null

    constructor(parcel: Parcel) : this() {
        score = parcel.readValue(Float::class.java.classLoader) as? Float
        aroma = parcel.readValue(Double::class.java.classLoader) as? Double
        mouthfeel = parcel.readValue(Double::class.java.classLoader) as? Double
        taste = parcel.readValue(Double::class.java.classLoader) as? Double
        appearance = parcel.readValue(Double::class.java.classLoader) as? Double
        overall = parcel.readValue(Double::class.java.classLoader) as? Double
        reviewCount = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(score)
        parcel.writeValue(aroma)
        parcel.writeValue(mouthfeel)
        parcel.writeValue(taste)
        parcel.writeValue(appearance)
        parcel.writeValue(overall)
        parcel.writeValue(reviewCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }


}