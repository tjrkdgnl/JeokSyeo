package com.jeoksyeo.wet.activity.editprofile

import android.content.Context
import com.vuforia.engine.wet.databinding.EditProfileBinding

interface EditProfileContract {

    interface BaseView{

        fun setGender_Man()
        fun setGender_Woman()

        fun resultNickNameCheck(result:Boolean)

        fun getView(): EditProfileBinding
    }


    interface BasePresenter{
        var view:BaseView

        fun executeEditProfile(name:String?,gender:String?,birthday:String?,image:String?)

        fun checkNickName(context: Context,name:String)

        fun checkLogin(context: Context,provider:String?)

    }


}