package com.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fragment.login.Fragment_nickName
import com.model.area.AreaList

class SignUpViewModel : ViewModel() {
    var buttonState: MutableLiveData<Boolean> = MutableLiveData(false)
        set(value) {
            field = value
        }

    private val emptyAreaList = AreaList().apply {
        name=""
        code=""
    }

    var stateArea= MutableLiveData(emptyAreaList)

    var countryArea =MutableLiveData(emptyAreaList)

    var townArea = MutableLiveData(emptyAreaList)

    var lock =false
        set(value){
            buttonState.value=value
            field =value
        }

    var checkRequest=false

    var nickname:String? = null

    var depth =0
}