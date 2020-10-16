package com.jeoksyeo.wet.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    private var buttonState: MutableLiveData<Boolean> = MutableLiveData(false)


    fun setButtonState(bool: Boolean) {
        buttonState.value = bool
    }

    fun getButtonState(): LiveData<Boolean> {
        return buttonState
    }

}