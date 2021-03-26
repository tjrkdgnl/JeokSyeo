package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel :ViewModel() {

    //메인액티비티에서 보여지는 바텀네비게이션의 show / hide 여부 결정하기 위해서 선언
    var bottomNavigationViewVisiblity = MutableLiveData(-1)

}