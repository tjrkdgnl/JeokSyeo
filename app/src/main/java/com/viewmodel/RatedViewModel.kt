package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RatedViewModel : ViewModel() {

    //사용자가 평가한 리뷰 개수 표시. 만약 유저가 삭제하면 실시간으로 데이터를 감소시켜야함.
    var reviewCount =MutableLiveData(0)
}