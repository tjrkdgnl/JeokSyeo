package com.model.my_comment

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Comment() :Parcelable {
    @SerializedName("review_id")
    @Expose
    var reviewId: String? = null

    @SerializedName("contents")
    @Expose
    var contents: String? = null

    @SerializedName("aroma")
    @Expose
    var aroma: Double? = null

    @SerializedName("appearance")
    @Expose
    var appearance: Double? = null

    @SerializedName("mouthfeel")
    @Expose
    var mouthfeel: Double? = null

    @SerializedName("overall")
    @Expose
    var overall: Double? = null

    @SerializedName("taste")
    @Expose
    var taste: Double? = null

    @SerializedName("score")
    @Expose
    var score: Double? = null

    @SerializedName("review_like_count")
    @Expose
    var reviewLikeCount: Int? = null

    @SerializedName("review_dislike_count")
    @Expose
    var reviewDislikeCount: Int? = null

    @SerializedName("alcohol")
    @Expose
    var alcohol: Alcohol? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Int? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Int? = null

    constructor(parcel: Parcel) : this() {
        reviewId = parcel.readString()
        contents = parcel.readString()
        aroma = parcel.readValue(Double::class.java.classLoader) as? Double
        appearance = parcel.readValue(Double::class.java.classLoader) as? Double
        mouthfeel = parcel.readValue(Double::class.java.classLoader) as? Double
        overall = parcel.readValue(Double::class.java.classLoader) as? Double
        taste = parcel.readValue(Double::class.java.classLoader) as? Double
        score = parcel.readValue(Double::class.java.classLoader) as? Double
        reviewLikeCount = parcel.readValue(Int::class.java.classLoader) as? Int
        reviewDislikeCount = parcel.readValue(Int::class.java.classLoader) as? Int
        alcohol = parcel.readParcelable(Alcohol::class.java.classLoader)
        createdAt = parcel.readValue(Int::class.java.classLoader) as? Int
        updatedAt = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reviewId)
        parcel.writeString(contents)
        parcel.writeValue(aroma)
        parcel.writeValue(appearance)
        parcel.writeValue(mouthfeel)
        parcel.writeValue(overall)
        parcel.writeValue(taste)
        parcel.writeValue(score)
        parcel.writeValue(reviewLikeCount)
        parcel.writeValue(reviewDislikeCount)
        parcel.writeParcelable(alcohol, flags)
        parcel.writeValue(createdAt)
        parcel.writeValue(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}