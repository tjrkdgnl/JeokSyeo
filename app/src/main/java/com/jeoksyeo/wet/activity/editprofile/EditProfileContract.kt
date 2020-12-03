package com.jeoksyeo.wet.activity.editprofile

import android.app.Activity
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
        fun checkOkButton(nicknameDuplicate:Boolean =false)
    }


    interface BasePresenter{
        var view:BaseView
        var activity:Activity

        fun executeEditProfile(context:Context,name: String, gender: String, birthday: String)

        fun checkNickName(context: Context)

        fun settingUserInfo(context: Context, provider:String?)

        fun imageUpload(context: Context,imageFile: File?)
    }
}