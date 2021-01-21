package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel :ViewModel() {


    var networkCheck = MutableLiveData<Boolean>(false)

    var bottomNavigationViewVisiblity = MutableLiveData<Int>(-1)


}