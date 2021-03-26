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

    //버튼 활성화 여부 결정
    var buttonState: MutableLiveData<Boolean> = MutableLiveData(false)

    //지역에 대한 정보 저장
    var locationMap = HashMap<String,AreaList?>()

    var nickname:String? = null

    var birthDay:String? =null

    var gender:String? =null
}