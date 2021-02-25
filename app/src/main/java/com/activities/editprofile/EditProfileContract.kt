package com.activities.editprofile

import android.content.Context
import com.activities.comment.CommentContract
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.EditProfileBinding
import java.io.File

interface EditProfileContract {

    interface EditProfileView:BaseView<EditProfileBinding>{
        fun setGender_Man()

        fun setGender_Woman()

        fun setBirthDay()

        fun resultNickNameCheck(result:Boolean)

        fun checkOkButton()

        fun setStatusBarInit()

    }


    interface EditProfilePresenter:BasePresenter<EditProfileBinding>{
        /**
         * 액티비티로부터 구현된 EditProfileView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        var view: EditProfileView


        fun executeEditProfile(context:Context,name: String, gender: String, birthday: String)

        fun checkNickName(context: Context)

        fun settingUserInfo(context: Context, provider:String?)

        fun imageUpload(context: Context,imageFile: File?)

        fun changeUserInfo()


        fun detach()

    }
}