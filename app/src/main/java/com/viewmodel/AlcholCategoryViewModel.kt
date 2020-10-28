package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.GlobalApplication

class AlcholCategoryViewModel: ViewModel() {

    var currentSort:String = GlobalApplication.DEFAULT_SORT


    val alcholTotalCount = MutableLiveData<Int>(0)

}