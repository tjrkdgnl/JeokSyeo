package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel :ViewModel() {

    var summaryCount =MutableLiveData<Int>(0)
    var currentPosition =MutableLiveData<Int>(-1)

    var alcoholTypeList = mutableListOf<Int>(0,0,0,0,0,0)
}