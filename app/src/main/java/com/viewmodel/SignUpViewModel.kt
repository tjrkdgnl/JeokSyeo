package com.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.model.area.AreaList

class SignUpViewModel : ViewModel() {
    var buttonState: MutableLiveData<Boolean> = MutableLiveData(false)
        set(value) {
            field = value
        }

    private var checkSignUp: MutableLiveData<Boolean> = MutableLiveData(false)
        set(value) {
            field = value
        }

    private val emptyAreaList = AreaList().apply {
        name=""
        code=""
    }

    var stateArea= MutableLiveData<AreaList>(emptyAreaList)

    var countryArea =MutableLiveData<AreaList>(emptyAreaList)

    var townArea = MutableLiveData<AreaList>(emptyAreaList)

    var lock =false
        set(value){
            buttonState.value=value
            field =value
        }

    var checkRequest=false

}