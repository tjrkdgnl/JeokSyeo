package com.activities.editprofile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.application.GlobalApplication
import com.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.EditProfileBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class EditProfile : BaseActivity<EditProfileBinding>(), View.OnClickListener,
    DatePicker.OnDateChangedListener,
    EditProfileContract.EditProfileView, View.OnKeyListener, TextWatcher {
    private val PICK_FROM_ALBUM = 1
    private var tempFile: File? = null
    private lateinit var presenter: Presenter
    private lateinit var gender: String
    private lateinit var birthday: String
    private var savingUri: Uri? = null
    override val layoutResID: Int = R.layout.edit_profile

    override fun setOnCreate() {
        presenter = Presenter().apply {
            viewObj = this@EditProfile
            activity = this@EditProfile
        }

        //최대 날짜 지정
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -15)
        binding.editProfileBasicDatePicker.datePicker.maxDate = calendar.time.time

        //datePicker 유저가 설정한 날짜로 지정
        val date = GlobalApplication.userInfo.birthDay.split("-")
        binding.editProfileBasicDatePicker.datePicker.init(
            date[0].toInt(), date[1].toInt(),
            date[2].toInt(),
            this
        )

        presenter.initUserInfo(GlobalApplication.userInfo.getProvider())

        setStatusBarInit()

        binding.insertInfoEditText.setOnKeyListener(this)
        binding.insertInfoEditText.addTextChangedListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.editProfileBasicDatePicker.datePicker.setOnDateChangedListener(this)
        }
    }

    override fun destroyPresenter() {
        presenter.detach()
    }

    //카메라 접근 시, 유저에게 동의를 구하는 다이얼로그 띄움
    private fun CameraPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                goAlbum()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(R.string.permission_2)
            .setDeniedMessage(R.string.permission_1)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()
    }

    //앨범으로 이동하여 유저가 선택한 이미지를 불러옴.
    private fun goAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()

            tempFile?.let {
                if (it.exists()) {
                    if (it.delete()) {
                        Log.e("error", it.absolutePath + " 삭제 성공")
                        tempFile = null
                    }
                }
            }
            return
        }

        when (requestCode) {
            //유저가 앨범에서 이미지를 선택한 경우, 돌아오는 콜백
            PICK_FROM_ALBUM -> {
                val photoUri = data?.data
                cropImage(photoUri)
            }

            //크롭 화면이 모두 완료된 이후, 다시 돌아오는 콜백
            UCrop.REQUEST_CROP -> {
                setImage()
            }

            UCrop.RESULT_ERROR -> {
                data?.let {
                    val throwable = UCrop.getError(it)
                    throwable?.printStackTrace()
                }
            }
        }
    }

    /**
     * 유저가 이미지를 크롭하면 임시로 만들어놓은 파일을 통해서 uri가 생성된다.
     * 이것이 {saveUri}이며 이를 통해서 이미지를 로딩한다.
     */
    private fun setImage() {
        presenter.imageUpload(savingUri)
        Glide.with(this)
            .load(savingUri?.path)
            .apply(
                RequestOptions()
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
            )
            .into(binding.editProfileImg)
    }

    /**
     * 앨범에서 선택한 이미지를 크롭화면에서 전달받는다. 또한 크롭하게 되는 이미지 저장은 uri에 하게된다.
     */
    private fun cropImage(photoUri: Uri?) {
        if (tempFile == null) {
            try {
                tempFile = presenter.createImageFile()
            } catch (e: Exception) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                finish()
                e.printStackTrace()
            }
        }
        //크롭 후 저장할 Uri
        val options = UCrop.Options()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            options.setStatusBarColor(getColor(R.color.orange))         //상태바 배경색
            options.setToolbarColor(getColor(R.color.orange))           // toolbar의 배경색상
            options.setToolbarWidgetColor(getColor(R.color.white))      //toolbar 내에 있는 view들의 색상
        } else {
            options.setStatusBarColor(
                ContextCompat
                    .getColor(this, R.color.orange)
            )                                                           //상태바 배경색
            options.setToolbarColor(
                ContextCompat
                    .getColor(this, R.color.orange)
            )                                                           // toolbar의 배경색상
            options.setToolbarWidgetColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )                                                           //toolbar 내에 있는 view들의 색상
        }

        options.setCircleDimmedLayer(true)                              // crop shape
        options.setToolbarTitle("Edit Profile")                         //title 지정
        options.setShowCropGrid(false)                                  // 격자표시
        options.setShowCropFrame(false)                                 //crop의 사각형 틀 유무

        savingUri = Uri.fromFile(tempFile)                              //임시 파일을 통해 이미지를 가르키는 uri 생성

        photoUri?.let { resource ->
            savingUri?.let { destination ->
                UCrop.of(resource, destination)                         //크롭한 이미지를 저장할 곳 설정
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(
                        GlobalApplication.instance.device_width.toInt(),
                        GlobalApplication.instance.device_height.toInt()
                    )
                    .withOptions(options)
                    .start(this)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editProfile_G_album -> CameraPermission()

            R.id.editProfile_G_imageButton_gender_man -> setGender_Man()

            R.id.editProfile_G_imageButton_gender_woman -> setGender_Woman()

            R.id.birthdayLinearLayout -> binding.editProfileBasicDatePicker.ExpandableDatePicker.toggle()

            R.id.button_datePicker_ok -> binding.editProfileBasicDatePicker.ExpandableDatePicker.toggle()

            R.id.editProfile_G_okButton -> presenter.executeEditProfile(
                binding.insertInfoEditText.text.toString(), gender, birthday
            )
        }
    }

    override fun checkOkButton() {
        if (presenter.enableToNickName) {
            binding.editProfileGOkButton.isEnabled =
                GlobalApplication.userInfo.nickName != binding.insertInfoEditText.text.toString() ||
                        GlobalApplication.userInfo.birthDay != birthday ||
                        GlobalApplication.userInfo.gender != gender ||
                        presenter.imageData != null
        }
        else{
            binding.editProfileGOkButton.isEnabled =false
        }
    }

    override fun setGender_Man() {
        binding.editProfileGImageButtonGenderWoman.setImageResource(R.mipmap.gender_checkbox_empty)
        binding.editProfileGImageButtonGenderMan.setImageResource(R.mipmap.gender_checkbox_full)
        gender = "M"
        checkOkButton()
    }

    override fun setGender_Woman() {
        binding.editProfileGImageButtonGenderMan.setImageResource(R.mipmap.gender_checkbox_empty)
        binding.editProfileGImageButtonGenderWoman.setImageResource(R.mipmap.gender_checkbox_full)
        gender = "F"
        checkOkButton()
    }

    override fun setBirthDay() {
        GlobalApplication.userInfo.birthDay.let {
            val birth = it.split("-")
            binding.birthdayYear.text = birth[0]
            binding.birthdayMonth.text = birth[1]
            binding.birthdayDay.text = birth[2]
        }
        birthday = GlobalApplication.userInfo.birthDay
    }


    override fun getBindingObj(): EditProfileBinding {
        return binding
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun confirmNickname(confirm: Boolean) {
        if (confirm) {
            checkOkButton()
            binding.checkNickNameText.visibility = View.VISIBLE
            binding.checkNickNameText.text = resources.getString(R.string.useNickName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.insertNameLinearLayout.background =
                    resources.getDrawable(R.drawable.bottom_line_green, null)
            } else {
                binding.insertNameLinearLayout.background =
                    ContextCompat.getDrawable(this, R.drawable.bottom_line_green)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.checkNickNameText.setTextColor(
                    resources.getColor(R.color.green, null)
                )
            } else {
                binding.checkNickNameText.setTextColor(
                    ContextCompat.getColor(this, R.color.green)
                )
            }

        } else {
            binding.checkNickNameText.visibility = View.VISIBLE
            binding.checkNickNameText.text = resources.getString(R.string.dontUseNickName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.insertNameLinearLayout.background =
                    resources.getDrawable(R.drawable.bottom_line_red, null)
            } else {
                binding.insertNameLinearLayout.background =
                    ContextCompat.getDrawable(this, R.drawable.bottom_line_red)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.checkNickNameText.setTextColor(resources.getColor(R.color.red, null))
            } else {
                binding.checkNickNameText.setTextColor(
                    ContextCompat.getColor(this, R.color.red)
                )
            }
        }
    }

    override fun setStatusBarInit() {
        //타이틀 변경
        binding.editBasicHeader.basicHeaderWindowName.text = "개인정보 수정"

        //디바이스에 따라 텍스트 사이즈 셋팅
        binding.editBasicHeader.basicHeaderWindowName.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            GlobalApplication.instance.getCalculatorTextSize(16f)
        )

        //status bar 배경변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.white, null)
            } else {
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            }
        }

        //status bar의 icon 색상 변경
        val decor = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or decor.systemUiVisibility
        } else {
            decor.systemUiVisibility = 0
        }
    }

    //키패드에서 엔터를 클릭 시, 키패드 내려가도록 구현
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            GlobalApplication.instance.keyPadSetting(binding.insertInfoEditText, this)
            return true
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        presenter.checkNickName()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    //dataPicker에서 입력하는 값으로 birthday 객체 생성
    @SuppressLint("SetTextI18n")
    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        birthday = year.toString()

        binding.birthdayYear.text = year.toString()

        if (monthOfYear + 1 < 10) binding.birthdayMonth.text = "0${monthOfYear + 1}"
        else binding.birthdayMonth.text = (monthOfYear + 1).toString()

        if (dayOfMonth < 10) binding.birthdayDay.text = "0$dayOfMonth"
        else binding.birthdayDay.text = dayOfMonth.toString()

        birthday += "-" + binding.birthdayMonth.text + "-" + binding.birthdayDay.text
        checkOkButton()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}