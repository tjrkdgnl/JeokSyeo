package com.model.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class MainClass() :Parcelable {
    @SerializedName("first_class")
    @Expose
    var firstClass: FirstClass? = null

    @SerializedName("second_class")
    @Expose
    var secondClass: SecondClass? = null

    @SerializedName("third_class")
    @Expose
    var thirdClass: ThirdClass? = null

    @SerializedName("fourth_class")
    @Expose
    var fourthClass: FourthClass? = null

    @SerializedName("fifth_class")
    @Expose
    var fifthClass: FifthClass? = null

    constructor(parcel: Parcel) : this() {
        firstClass = parcel.readParcelable(FirstClass::class.java.classLoader)
        secondClass = parcel.readParcelable(SecondClass::class.java.classLoader)
        thirdClass = parcel.readParcelable(ThirdClass::class.java.classLoader)
        fourthClass = parcel.readParcelable(FourthClass::class.java.classLoader)
        fifthClass = parcel.readParcelable(FifthClass::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(firstClass, flags)
        parcel.writeParcelable(secondClass, flags)
        parcel.writeParcelable(thirdClass, flags)
        parcel.writeParcelable(fourthClass, flags)
        parcel.writeParcelable(fifthClass, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainClass> {
        override fun createFromParcel(parcel: Parcel): MainClass {
            return MainClass(parcel)
        }

        override fun newArray(size: Int): Array<MainClass?> {
            return arrayOfNulls(size)
        }
    }


}