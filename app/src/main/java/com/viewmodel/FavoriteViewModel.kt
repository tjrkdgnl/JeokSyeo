package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel : ViewModel() {

    var summaryCount = MutableLiveData<Int>(0)
    var currentPosition = MutableLiveData<Int>(-1)

    var viewPagerPosition = 0

    fun setPosition(position: Int) {
        if (position == viewPagerPosition) {
            currentPosition.value = position
        }
    }

    var alcoholTypeList = mutableListOf<Int>(0, 0, 0, 0, 0, 0)
}