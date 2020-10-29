package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.GlobalApplication

class AlcholCategoryViewModel: ViewModel() {

    //보기 방식을 바꿨을 때 현재 sort에 따라서 셋팅되어야함.
    var currentSort:String = GlobalApplication.DEFAULT_SORT

    val viewModelCurrentPosition =MutableLiveData<Int>(0)

}