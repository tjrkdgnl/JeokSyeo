package com.jeoksyeo.wet.activity.editprofile

import android.app.Activity
import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.model.edit_profile.Profile
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.regex.Pattern

class Presenter : EditProfileContract.BasePresenter {
    override lateinit var view: EditProfileContract.BaseView
    override lateinit var activity: Activity
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var profile:Profile? =null

    override fun executeEditProfile(context:Context,name: String?, gender: String?, birthday: String?) {
        val map = HashMap<String,Any>()
        profile?.let { map.put("profile",it) }
        name?.let { map.put("nickname", it) }
        birthday?.let { map.put("birth", it) }
        gender?.let { map.put("gender", it) }

        view.getView().editProfileGOkButton.setEditButtonClickListener {
            val check = JWTUtil.settingUserInfo(false)
            if(check){
                settingProgressBar(true)
                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .editProfile(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.data?.result?.let {
                            if(it =="SUCCESS"){
                                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                                    .getUserInfo(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({user->
                                        settingProgressBar(false)
                                        GlobalApplication.userInfo.apply {
                                            this.nickName =user.data?.userInfo?.nickname
                                            this.birthDay = user.data?.userInfo?.birth
                                            this.gender = user.data?.userInfo?.gender
                                            this.profileImg = user.data?.userInfo?.profile
                                        }
                                        Toast.makeText(context,"수정이 완료되었습니다.",Toast.LENGTH_SHORT).show()

                                        if(context is Activity){
                                            context.finish()
                                            context.overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
                                        }

                                    },{t->
                                        settingProgressBar(false)
                                        Log.e(ErrorManager.USERINFO,t.message.toString())}
                                    ))
                            }
                            else{
                                settingProgressBar(false)
                                Toast.makeText(context,"수정이 제대로 이뤄지지 않았습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    },{t->
                        settingProgressBar(false)
                        Log.e(ErrorManager.EDIT_PROFILE,t.message.toString())
                    }))
            }
            else{
                CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_MAIN,true)
            }
        }
    }

    override fun checkNickName(context: Context, name: String) {
        val check = Pattern.matches("^\\w+|[가-힣]+$", name)

        if (name.isNotEmpty()) {
            if (check) {
                //api 설정
                compositeDisposable.add(
                    ApiGenerator.retrofit.create(ApiService::class.java)
                        .checkNickName(GlobalApplication.userBuilder.createUUID, name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //result=true면 닉네임 중복을 의미
                            it.data?.result?.let { bool ->
                                if (!bool) {
                                    view.getView().checkNickNameText.visibility = View.VISIBLE
                                    view.getView().checkNickNameText.text =
                                        context.getString(R.string.useNickName)
                                    view.getView().insertNameLinearLayout.background =
                                        context.resources.getDrawable(
                                            R.drawable.bottom_line_green,
                                            null
                                        )
                                    view.getView().checkNickNameText.setTextColor(
                                        context.resources.getColor(
                                            R.color.green,
                                            null
                                        )
                                    )
                                }
                            }
                        }, { t -> Log.e(ErrorManager.NICKNAME_DUPLICATE, t.message.toString()) })
                )

            } else {
                view.getView().checkNickNameText.visibility = View.VISIBLE
                view.getView().checkNickNameText.text = context.getString(R.string.dontUseNickName)
                view.getView().insertNameLinearLayout.background =
                    context.resources.getDrawable(R.drawable.bottom_line_red, null)
                view.getView().checkNickNameText.setTextColor(
                    context.resources.getColor(
                        R.color.red,
                        null
                    )
                )
            }
        } else {
            view.getView().insertNameLinearLayout.background =
                context.resources.getDrawable(R.drawable.bottom_line, null)
            view.getView().checkNickNameText.visibility = View.INVISIBLE
        }
    }

    override fun settingUserInfo(context: Context, provider: String?) {
        provider?.let {
            view.getView().insertInfoEditText.setText(GlobalApplication.userInfo.nickName)

            GlobalApplication.userInfo.birthDay?.let {
                view.setBirthDay()
            }
            GlobalApplication.userInfo.gender?.let {
                if (it == "M") {
                    view.setGender_Man()
                }
                else if( it =="F")
                    view.setGender_Woman()
            }
            GlobalApplication.userInfo.getProfile()?.let{ lst->
                if(lst.isNotEmpty()){
                    Glide.with(context)
                        .load(lst[0].mediaResource?.small?.src.toString())
                        .apply(
                            RequestOptions()
                                .signature(ObjectKey("signature"))
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .circleCrop())
                        .into(view.getView().editProfileImg)
                }
            }
        }
    }

    override fun imageUpload(context: Context,imageFile: File?) {

        Log.e("path",imageFile?.name.toString())

        val check = JWTUtil.settingUserInfo(false)

        val imageBody =imageFile?.asRequestBody("image/jpg".toMediaTypeOrNull())

        val file = imageFile?.name?.let {name->
            imageBody?.let { body ->
                MultipartBody.Part.createFormData("file", name, body)
            } }

        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .imageUpload(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    result.data?.mediaId?.let {
                        profile = Profile("image",it)
                    }
                },{ t->
                    Log.e(ErrorManager.IMAGE_UPLOAD,t.message.toString())
                }))
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_MAIN,true)
        }
    }

    private fun settingProgressBar(check:Boolean){
        if(check){
            view.getView().progressBarLoading.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            view.getView().progressBarLoading.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        }
    }

    private fun View.setEditButtonClickListener(onClick:(View)->Unit){
        val oneClick = OneClickListener{
            onClick(it)
        }
        setOnClickListener(oneClick)
    }

}