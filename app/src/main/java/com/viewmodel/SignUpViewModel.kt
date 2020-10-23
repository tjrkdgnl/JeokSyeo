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

    var stateArea= MutableLiveData<AreaList>(null)

    var countryArea =MutableLiveData<AreaList>(null)

    var townArea = MutableLiveData<AreaList>(null)

    var lock =false
        set(value){
            buttonState.value=value
            field =value
        }
}