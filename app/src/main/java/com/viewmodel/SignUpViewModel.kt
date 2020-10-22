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

    var stateSelectButton =MutableLiveData<Boolean>(false)
        set(value) {
            field =value
            if(countrySelectButton.value!!){
                buttonState.value =true
            }
        }
    var stateName:String? =null
    var countrySelectButton =MutableLiveData<Boolean>(false)
        set(value) {
            field =value
            if(stateSelectButton.value!!){
                buttonState.value =true
            }
        }
    var countryName:String? =null

    var code:String? =null
}