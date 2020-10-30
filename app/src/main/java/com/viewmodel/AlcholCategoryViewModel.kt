package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.GlobalApplication

class AlcholCategoryViewModel: ViewModel() {

    //보기 방식을 바꿨을 때 현재 sort에 따라서 셋팅되어야함.
    var currentSort:String = GlobalApplication.DEFAULT_SORT
    var currentPosition = MutableLiveData<Int>(0)
    var refreshCheck =false

   private val alcholTotalCountList  = mutableListOf<Int>(-1,-1,-1,-1,-1)

    fun setCount(count:Int){
        alcholTotalCountList[currentPosition.value!!] = count
    }

    fun getCount(position:Int):Int{
        return alcholTotalCountList[position]
    }

}