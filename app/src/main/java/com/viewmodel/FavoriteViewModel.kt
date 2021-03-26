package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel : ViewModel() {

    var summaryCount = MutableLiveData(0)

    //현재 뷰페이저의 포지션으로 이를 통해 alcoholTypeList로부터 주류 토탈값을 불러온다.
    var currentPosition = MutableLiveData(-1)

    var viewPagerPosition = 0

    fun setPosition(position: Int) {
        if (position == viewPagerPosition) {
            currentPosition.value = position
        }
    }

    var alcoholTypeList = mutableListOf(0, 0, 0, 0, 0, 0)
}