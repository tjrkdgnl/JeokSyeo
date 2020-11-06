package com.jeoksyeo.wet.activity.editprofile

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.model.edit_profile.Profile
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
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var profile:Profile? =null

    override fun executeEditProfile(context:Context,name: String?, gender: String?, birthday: String?) {
        val check = JWTUtil.settingUserInfo(false)

        val map = HashMap<String,Any>()
        profile?.let { map.put("profile",it) }
        name?.let {
            Log.e("name",it)
            map.put("nickname", it) }
        birthday?.let {
            Log.e("birth",it)
            map.put("birth", it) }
        gender?.let {
            Log.e("gender",it)
            map.put("gender", it) }

        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .editProfile(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.result?.let {
                        if(it =="SUCCESS"){
                            Toast.makeText(context,"수정이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context,"수정이 제대로 이뤄지지 않았습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                },{t->Log.e(ErrorManager.EDIT_PROFILE,t.message.toString())
                }))
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_MAIN,true)
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

    override fun checkLogin(context: Context, provider: String?) {
        provider?.let {
            view.getView().insertInfoEditText.setText(GlobalApplication.userInfo.getNickName())

            GlobalApplication.userInfo.getBirthDay()?.let {
                view.setBirthDay()
            }
            GlobalApplication.userInfo.getGender()?.let {
                if (it.equals("M")) {
                    view.setGender_Man()
                } else
                    view.setGender_Woman()
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


}