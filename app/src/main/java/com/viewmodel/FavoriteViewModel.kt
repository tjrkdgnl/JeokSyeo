package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel :ViewModel() {


    var currentPosition =MutableLiveData<Int>()

    var totalFavoriteCount = 0

    var alcoholTypeList = mutableListOf<Int>(0,0,0,0,0,0)

}