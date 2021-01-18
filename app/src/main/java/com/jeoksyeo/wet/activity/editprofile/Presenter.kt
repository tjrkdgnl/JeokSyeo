package com.jeoksyeo.wet.activity.editprofile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.model.edit_profile.Profile
import com.model.user.profileToPojo.ProfileInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.service.NetworkUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.regex.Pattern

class Presenter : EditProfileContract.BasePresenter {
    override lateinit var view: EditProfileContract.BaseView
    override lateinit var activity: Activity
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    var profile: Profile? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var changeObject: ProfileInfo
    private lateinit var networkUtil: NetworkUtil
    var checkDuplicate = false



    override fun setNetworkUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(activity)
            networkUtil.register()
        }

    }

    override fun executeEditProfile(
        context: Context,
        name: String,
        gender: String,
        birthday: String
    ) {
        changeObject = ProfileInfo()

        if (profile != null) {
            changeObject.profile = com.model.user.profileToPojo.Profile()
            changeObject.profile!!.type = profile!!.type
            changeObject.profile!!.mediaId = profile!!.media_id
            changeObject.nickname = name
            changeObject.birth = birthday
            changeObject.gender = gender
        } else {
            changeObject.nickname = name
            changeObject.birth = birthday
            changeObject.gender = gender
        }

        view.getView().editProfileGOkButton.setEditButtonClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val check = JWTUtil.checkToken()

                withContext(Dispatchers.Main) {
                    if (check) {
                        settingProgressBar(true)
                        compositeDisposable.add(
                            ApiGenerator.retrofit.create(ApiService::class.java)
                                .editProfile(
                                    GlobalApplication.userBuilder.createUUID,
                                    GlobalApplication.userInfo.getAccessToken(),
                                    changeObject
                                )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    it.data?.result?.let {
                                        if (it == "SUCCESS") {
                                            //유저 정보 갱신
                                            changeUserInfo()
                                        } else {
                                            settingProgressBar(false)
                                            Toast.makeText(
                                                context,
                                                "수정이 제대로 이뤄지지 않았습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }, { t ->
                                    settingProgressBar(false)
                                    CustomDialog.networkErrorDialog(context)
                                    Log.e(ErrorManager.EDIT_PROFILE, t.message.toString())
                                })
                        )
                    } else {
                        CustomDialog.loginDialog(
                            context,
                            GlobalApplication.ACTIVITY_HANDLING_MAIN,
                            true
                        )
                    }
                }
            }
        }
    }

    override fun changeUserInfo() {
        compositeDisposable.add(ApiGenerator.retrofit.create(
            ApiService::class.java
        )
            .getUserInfo(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                settingProgressBar(false)
                GlobalApplication.userInfo.apply {
                    user.data?.userInfo?.nickname?.let { nick ->
                        this.nickName = nick
                    }
                    user.data?.userInfo?.birth?.let { birth ->
                        this.birthDay = birth
                    }
                    user.data?.userInfo?.gender?.let { gender ->
                        this.gender = gender
                    }
                    this.profileImg =
                        user.data?.userInfo?.profile
                }
                Toast.makeText(
                    activity,
                    "수정이 완료되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                activity.finish()
                activity.overridePendingTransition(
                    R.anim.left_to_current,
                    R.anim.current_to_right
                )

            }, { t ->
                CustomDialog.networkErrorDialog(activity)
                settingProgressBar(false)
                Log.e(ErrorManager.USERINFO, t.message.toString())
            }
            ))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun checkNickName(context: Context) {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            CoroutineScope(Dispatchers.IO).launch {
                val loginCheck = JWTUtil.checkToken()

                withContext(Dispatchers.Main) {
                    if (loginCheck) {
                        var check = false
                        if (GlobalApplication.userInfo.nickName != view.getView().insertInfoEditText.text.toString()) {
                            check = Pattern.matches(
                                "^\\w+|[가-힣]+$", view.getView().insertInfoEditText.text.toString()
                            )

                            for(word in GlobalApplication.instance.getBanWordList()){
                                if(view.getView().insertInfoEditText.text.contains(word)){
                                    check = false
                                }
                            }

                            if (check) {
                                compositeDisposable.add(
                                    ApiGenerator.retrofit.create(ApiService::class.java)
                                        .checkNickName(
                                            GlobalApplication.userBuilder.createUUID,
                                            view.getView().insertInfoEditText.text.toString()
                                        )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                            {
                                                //result=true면 닉네임 중복을 의미
                                                it.data?.result?.let { bool ->
                                                    if (!bool) { //닉네임을 사용 할 수 있다면
                                                        checkNickname(context, true)
                                                    } else { //닉네임이 중복이라면
                                                        checkNickname(context, false)
                                                    }
                                                }
                                            },
                                            { t ->
                                                Log.e(
                                                    ErrorManager.NICKNAME_DUPLICATE,
                                                    t.message.toString()
                                                )
                                            })
                                )


                            } else {//닉네임 형식을 틀렸다면
                                checkNickname(context, false)
                            }
                        } else {
                            //닉네임을 바꾸지 않았다면,
                            checkDuplicate = false
                            view.checkOkButton()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                view.getView().insertNameLinearLayout.background =
                                    context.resources.getDrawable(R.drawable.bottom_line, null)
                            }
                            else{
                                view.getView().insertNameLinearLayout.background =
                                    ContextCompat.getDrawable(context,R.drawable.bottom_line)
                            }

                            view.getView().checkNickNameText.visibility = View.INVISIBLE
                        }
                    } else { //세션이 만료되었을 때
                        CustomDialog.loginDialog(activity, 0, true)
                    }
                }
            }
        }, 300)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkNickname(context: Context, right: Boolean) {
        if (right) {
            checkDuplicate = false
            view.checkOkButton()
            view.getView().checkNickNameText.visibility = View.VISIBLE
            view.getView().checkNickNameText.text =
                context.getString(R.string.useNickName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getView().insertNameLinearLayout.background =
                    context.resources.getDrawable(R.drawable.bottom_line_green, null)
            } else{
                view.getView().insertNameLinearLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.bottom_line_green)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.getView().checkNickNameText.setTextColor(
                    context.resources.getColor(R.color.green, null))
            }else{
                view.getView().checkNickNameText.setTextColor(
                    ContextCompat.getColor(context,R.color.green))
            }

        } else {
            checkDuplicate = true
            view.checkOkButton()
            view.getView().checkNickNameText.visibility = View.VISIBLE
            view.getView().checkNickNameText.text = context.getString(R.string.dontUseNickName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getView().insertNameLinearLayout.background =
                    context.resources.getDrawable(R.drawable.bottom_line_red, null)
            } else{
                view.getView().insertNameLinearLayout.background =
                    ContextCompat.getDrawable(context,R.drawable.bottom_line_red)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.getView().checkNickNameText.setTextColor(
                    context.resources.getColor(R.color.red, null))
            }else{
                view.getView().checkNickNameText.setTextColor(
                    ContextCompat.getColor(context,R.color.red))
            }
        }
    }

    override fun settingUserInfo(context: Context, provider: String?) {
        provider?.let {
            GlobalApplication.userInfo.nickName.let { nickname ->
                view.getView().insertInfoEditText.setText(nickname)

            }
            GlobalApplication.userInfo.birthDay.let {
                view.setBirthDay()

            }
            GlobalApplication.userInfo.gender.let {
                if (it == "M") {
                    view.setGender_Man()
                } else if (it == "F")
                    view.setGender_Woman()

            }

            GlobalApplication.userInfo.getProfile()?.let { lst ->
                if (lst.isNotEmpty()) {
                    Glide.with(context)
                        .load(lst[0].mediaResource?.small?.src.toString())
                        .apply(
                            RequestOptions()
                                .signature(ObjectKey(System.currentTimeMillis()))
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .circleCrop()
                        )
                        .into(view.getView().editProfileImg)
                }
            }
        }
    }

    override fun imageUpload(context: Context, imageFile: File?) {

        imageFile?.let { file ->
            val requestBody = RequestBody.create(MediaType.parse("image/jpg"),file)
            val body = MultipartBody.Part.createFormData("file","userImage",requestBody)
            CoroutineScope(Dispatchers.IO).launch {
                val check = JWTUtil.checkToken()

                withContext(Dispatchers.Main) {
                    if (check) {
                        compositeDisposable.add(
                            ApiGenerator.retrofit.create(ApiService::class.java)
                                .imageUpload(
                                    GlobalApplication.userBuilder.createUUID,
                                    GlobalApplication.userInfo.getAccessToken(),
                                    body
                                )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result ->
                                    result.data?.mediaId?.let {
                                        profile = Profile("image", it)
                                        view.checkOkButton()
                                    }
                                }, { t ->
                                    CustomDialog.networkErrorDialog(activity)
                                    Log.e(ErrorManager.IMAGE_UPLOAD, t.message.toString())
                                })
                        )
                    } else {
                        CustomDialog.loginDialog(
                            context,
                            GlobalApplication.ACTIVITY_HANDLING_MAIN,
                            true
                        )
                    }
                }
            }
        }
    }

    private fun settingProgressBar(check: Boolean) {
        if (check) {
            view.getView().progressBarLoading.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            view.getView().progressBarLoading.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        }
    }

    private fun View.setEditButtonClickListener(onClick: (View) -> Unit) {
        val oneClick = OneClickListener {
            onClick(it)
        }
        setOnClickListener(oneClick)
    }

    override fun detach() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }
    }
}