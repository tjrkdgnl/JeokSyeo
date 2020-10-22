package com.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    var buttonState: MutableLiveData<Boolean> = MutableLiveData(false)
        set(value) {
            field = value
        }

    private var checkSignUp: MutableLiveData<Boolean> = MutableLiveData(false)
        set(value) {
            field = value
        }

}