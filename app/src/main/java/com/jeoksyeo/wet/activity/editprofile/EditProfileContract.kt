package com.jeoksyeo.wet.activity.editprofile

import android.content.Context
import com.vuforia.engine.wet.databinding.EditProfileBinding
import java.io.File

interface EditProfileContract {

    interface BaseView{

        fun setGender_Man()
        fun setGender_Woman()
        fun setBirthDay()

        fun resultNickNameCheck(result:Boolean)

        fun getView(): EditProfileBinding
    }


    interface BasePresenter{
        var view:BaseView

        fun executeEditProfile(context:Context,name: String?, gender: String?, birthday: String?)

        fun checkNickName(context: Context,name:String)

        fun checkLogin(context: Context,provider:String?)

        fun imageUpload(context: Context,imageFile: File?)
    }
}