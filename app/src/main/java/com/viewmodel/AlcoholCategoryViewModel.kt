package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.GlobalApplication

class AlcoholCategoryViewModel: ViewModel() {
    //보기 방식을 바꿨을 때 현재 sort에 따라서 셋팅되어야함.
    var currentSort = MutableLiveData<String>(GlobalApplication.DEFAULT_SORT)

    //현재 뷰페이저의 페이지를 표시. 현재 포지션 값을 통해서 totalCountList에 저장된
    //토탈 값 호출
    var changePosition = MutableLiveData(0)

    var totalCountList = mutableListOf(0,0,0,0,0)
}