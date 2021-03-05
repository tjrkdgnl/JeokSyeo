package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.model.area.AreaList

class SignUpViewModel : ViewModel() {

    companion object{
        val CITY ="city"
        val MIDDLE_TOWN="middleTown"
        val SMALL_TOWN="smallTown"
    }


    var buttonState: MutableLiveData<Boolean> = MutableLiveData(false)

    var locationMap = HashMap<String,AreaList?>()

    var nickname:String? = null

    var birthDay:String? =null

    var gender:String? =null
}