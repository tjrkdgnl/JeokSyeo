package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel : ViewModel() {

    var summaryCount = MutableLiveData(0)
    var currentPosition = MutableLiveData(-1)

    var viewPagerPosition = 0

    fun setPosition(position: Int) {
        if (position == viewPagerPosition) {
            currentPosition.value = position
        }
    }

    var alcoholTypeList = mutableListOf(0, 0, 0, 0, 0, 0)
}