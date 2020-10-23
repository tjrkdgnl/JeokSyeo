package com.model.recommend_alchol

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Class<T> {
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
}