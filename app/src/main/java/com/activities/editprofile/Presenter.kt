package com.activities.editprofile

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
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
import com.model.edit_profile.ProfileData
import com.model.user.profileToPojo.Profile
import com.model.user.profileToPojo.ProfileInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Presenter : EditProfileContract.EditProfilePresenter {
    override var viewObj: EditProfileContract.EditProfileView? =null
    override  val view: EditProfileContract.EditProfileView by lazy {
        viewObj!!
    }
    override lateinit var activity: Activity
    override var enableToNickName = true

    override var imageData: ProfileData?=null
    private lateinit var changeObject: ProfileInfo
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun executeEditProfile(
        name: String,
        gender: String,
        birthday: String
    ) {
        /**
         * api에 파라미터를 전달하기 위해서 객체를 생성하고 셋팅한다.
         * 다른 API에서 처럼 map을 통해 자동 JSON 변경이 일어나도록 하는 패턴과 다르게
         * 객체값을 넘기는 이유는 객체안에 profile객체가 존재하므로 이는 map 형식으로 전송했을 때
         * JSON 변형이 일어나지 않기 때문
         */
        changeObject = ProfileInfo()

        //프로필 이미지가 있다면 같이 전송
        if(imageData !=null){
            changeObject.profile = Profile()
            changeObject.profile?.type = imageData?.type
            changeObject.profile?.mediaId = imageData?.media_id
            changeObject.nickname = name
            changeObject.birth = birthday
            changeObject.gender = gender
        }
        else{
            changeObject.nickname = name
            changeObject.birth = birthday
            changeObject.gender = gender
        }

        view.getBindingObj().editProfileGOkButton.setEditButtonClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val check = JWTUtil.checkAccessToken()

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
                                            updateUserInfo()
                                        } else {
                                            settingProgressBar(false)
                                            Toast.makeText(
                                                activity,
                                                "수정이 제대로 이뤄지지 않았습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }, { t ->
                                    settingProgressBar(false)
                                    CustomDialog.networkErrorDialog(activity)
                                    Log.e(ErrorManager.EDIT_PROFILE, t.message.toString())
                                })
                        )
                    } else {
                        CustomDialog.loginDialog(
                            activity,
                            GlobalApplication.ACTIVITY_HANDLING_MAIN,
                            true
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun createImageFile(): File? {
        val tempFile: File?

        // 이미지 파일 이름 ( JeokSyeo_{시간}_ )
        val timeStamp = SimpleDateFormat("HHmmss").format(Date())
        val imageFileName = "JeokSyeo_" + timeStamp + "_"

        // 이미지가 저장될 폴더 이름 ( JeokSyeo_ )
        val storageDir = File(activity.getExternalFilesDir(null).toString() + "/JeokSyeo/")
        if (!storageDir.exists()) storageDir.mkdirs()

        // 빈 파일 생성
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        tempFile = image.absoluteFile
        Log.e("파일생성", tempFile?.name.toString())
        return image
    }

    //유저의 정보 업데이트
    override fun updateUserInfo() {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getUserInfo(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                settingProgressBar(false)
                //업데이트 된 유저의 정보를 app단에서 업데이트 진행
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
                    this.profileImg = user.data?.userInfo?.profile
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
    override fun checkNickName() {
        with(Handler(Looper.getMainLooper())) {
            /**
             * 해당 메서드는 유저가 타이핑할 때마다 호출되기 때문에 호출 stack이 쌓일 수 있다.
             * 때문에 서버에서 쌓인 api에 대한 결과값을 리턴했을 때, ui상에서 보여지는 값에 대한 결과값이
             * 아닐 수 있기 때문에 호출 지연이 필요하다. 300밀리세컨드는 서버개발자가 호출한 api에 대한
             * 최소 응답시간이라고 정한 값이다.
             */
            removeCallbacksAndMessages(null)
            postDelayed({
                CoroutineScope(Dispatchers.IO).launch {
                    val loginCheck = JWTUtil.checkAccessToken()

                    withContext(Dispatchers.Main) {
                        if (loginCheck) {
                            if (GlobalApplication.userInfo.nickName != view.getBindingObj().insertInfoEditText.text.toString()) {

                                //정해 놓은 정규표현식 규칙에 따라 bool값 리턴
                                //특수문자 x, 공백 x
                                enableToNickName = Pattern.matches(
                                    "^\\w+|[가-힣]+$",
                                    view.getBindingObj().insertInfoEditText.text.toString()
                                )

                                //특정단어(관리자,적셔..etc)가 들어갈 시, 수정이 안되도록 검사
                                for (word in GlobalApplication.instance.getBanWordList()) {
                                    if (view.getBindingObj().insertInfoEditText.text.contains(word)) {
                                        enableToNickName = false
                                    }
                                }

                                //규칙에 부합하는 닉네임일 때, 중복을 체크한다.
                                //api로 유저가 입력한 닉네임을 파라미터로 전송하여 서버에서 결과 리턴
                                if (enableToNickName) {
                                    compositeDisposable.add(
                                        ApiGenerator.retrofit.create(ApiService::class.java)
                                            .checkNickName(
                                                GlobalApplication.userBuilder.createUUID,
                                                view.getBindingObj().insertInfoEditText.text.toString()
                                            )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                {
                                                    //result=true면 닉네임 중복을 의미
                                                    it.data?.result?.let { bool ->
                                                        if (!bool) { //닉네임을 사용 할 수 있다면
                                                            view.confirmNickname(true)
                                                        } else { //닉네임이 중복이라면
                                                            view.confirmNickname(false)
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
                                } else {
                                    //규칙에 부합하지 않은 닉네임인 경우
                                    view.checkOkButton()
                                    view.confirmNickname(false)
                                }
                            } else { //닉네임을 원래 닉네임으로 했을 경우, 기본 라인 색상으로 변경
                                enableToNickName = true
                                view.checkOkButton()

                                //버전으로 인한 분기처리
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    view.getBindingObj().insertNameLinearLayout.background =
                                        activity.resources.getDrawable(R.drawable.bottom_line, null)
                                } else {
                                    view.getBindingObj().insertNameLinearLayout.background =
                                        ContextCompat.getDrawable(activity, R.drawable.bottom_line)
                                }

                                view.getBindingObj().checkNickNameText.visibility = View.INVISIBLE
                            }
                        } else { //세션이 만료되었을 때
                            CustomDialog.loginDialog(activity, 0, true)
                        }
                    }
                }
            }, 300)
        }
    }

    override fun initUserInfo(provider: String?) {
        //프로바이더는 유저가 소셜로그인 진행 시, 회사의 이름이며 이는 로그인이 된 상태일 때 부여된 값이므로
        //반드시 갖고 있는 정보로 해당 값이 존재할 시, 유저의 정보를 갖고 있음을 알 수 있음.
        provider?.let {
            GlobalApplication.userInfo.nickName.let { nickname ->
                view.getBindingObj().insertInfoEditText.setText(nickname)
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

            //이미지 로딩
            GlobalApplication.userInfo.getProfile()?.let { lst ->
                //서버에서 저장되는 유저의 이미지는 리스트형식이다.
                if (lst.isNotEmpty()) {
                    Glide.with(activity)
                        .load(lst[lst.size-1].mediaResource?.small?.src.toString())
                        .apply(
                            RequestOptions()
                                .signature(ObjectKey(System.currentTimeMillis()))
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .circleCrop())
                        .into(view.getBindingObj().editProfileImg)
                }
            }
        }
    }

    override fun imageUpload(imageUri: Uri?) {
        //Uri의 path를 통해서 requestbody를 만들어준다.
        //requestbody를 통해서 서버로 전송하기 위한 multipart의 Part 타입으로 객체를 생성하여 이를 전송한다.
        imageUri?.path?.let { path ->
            val requestBody = RequestBody.create(MediaType.parse("image/jpg"), File(path))
            val body = MultipartBody.Part.createFormData("file", "userImage", requestBody)

            CoroutineScope(Dispatchers.IO).launch {
                val check = JWTUtil.checkAccessToken()

                withContext(Dispatchers.Main) {
                    if (check) {
                        compositeDisposable.add(
                            ApiGenerator.retrofit.create(ApiService::class.java)
                                .imageUpload(
                                    GlobalApplication.userBuilder.createUUID,
                                    GlobalApplication.userInfo.getAccessToken(),
                                    body)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    //서버에 전송할 이미지 데이터 객체 셋팅
                                    it.data?.mediaId?.let {id->
                                        imageData = ProfileData("image",id)
                                    }
                                    view.checkOkButton()
                                }, { t ->
                                    CustomDialog.networkErrorDialog(activity)
                                    Log.e(ErrorManager.IMAGE_UPLOAD, t.message.toString())
                                })
                        )
                    } else {
                        CustomDialog.loginDialog(
                            activity,
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
            view.getBindingObj().progressBarLoading.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            view.getBindingObj().progressBarLoading.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    //확장함수를 통해 연속 클릭 제한
    private fun View.setEditButtonClickListener(onClick: (View) -> Unit) {
        val oneClick = OneClickListener {
            onClick(it)
        }
        setOnClickListener(oneClick)
    }

    override fun detach() {
        compositeDisposable.dispose()
        viewObj=null
    }
}