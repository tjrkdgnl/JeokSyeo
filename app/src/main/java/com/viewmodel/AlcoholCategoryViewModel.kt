package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.GlobalApplication

class AlcoholCategoryViewModel: ViewModel() {

    companion object{
         val LIST_TOGGLE = "list"
         val GRID_TOGGLE = "grid"
    }


    //보기 방식을 바꿨을 때 현재 sort에 따라서 셋팅되어야함.
    var currentSort = MutableLiveData<String>(GlobalApplication.DEFAULT_SORT)

    var changePosition = MutableLiveData(0)


    var totalCountList = mutableListOf(0,0,0,0,0)


}