package com.jeoksyeo.wet.activity.editprofile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfile : AppCompatActivity(), View.OnClickListener, DatePicker.OnDateChangedListener,
    EditProfileContract.BaseView, View.OnKeyListener, TextWatcher {
    private lateinit var binding: EditProfileBinding
    private val PICK_FROM_ALBUM = 1
    private var tempFile: File? = null
    private lateinit var presenter: Presenter
    private lateinit var gender: String
    private lateinit var birthday: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.edit_profile)

        presenter = Presenter().apply {
            view = this@EditProfile
            activity = this@EditProfile
        }

        //최대 날짜 지정
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR,-15)
        binding.editProfileBasicDatePicker.datePicker.maxDate = calendar.time.time

        //datePicker 유저가 설정한 날짜로 지정
        val date = GlobalApplication.userInfo.birthDay.split("-")
        binding.editProfileBasicDatePicker.datePicker.init(
            date[0].toInt()
            ,date[1].toInt(),
            date[2].toInt(),
            this
        )

        presenter.settingUserInfo(this,GlobalApplication.userInfo.getProvider())
        binding.editBasicHeader.basicHeaderWindowName.text = "개인정보 수정"


        binding.insertInfoEditText.setOnKeyListener(this)
        binding.insertInfoEditText.addTextChangedListener(this)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            binding.editProfileBasicDatePicker.datePicker.setOnDateChangedListener(this)
        }
    }

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

    private fun goAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
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
            PICK_FROM_ALBUM -> {
                val photoUri = data?.data
                cropImage(photoUri)
            }

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

    private fun setImage() {
        presenter.imageUpload(this,tempFile)
        Glide.with(this)
            .load(tempFile?.absolutePath)
            .apply(
                RequestOptions()
                    .signature(ObjectKey("signature"))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
            )
            .into(binding.editProfileImg)
    }

    private fun cropImage(photoUri: Uri?) {
        if (tempFile == null) {
            try {
                tempFile = createImageFile()
            } catch (e: Exception) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                finish()
                e.printStackTrace()
            }
        }
        //크롭 후 저장할 Uri
        val options = UCrop.Options()
        options.setStatusBarColor(getColor(R.color.orange)) //상태바 배경색
        options.setToolbarColor(getColor(R.color.orange)) // toolbar의 배경색상
        options.setToolbarWidgetColor(getColor(R.color.white)) //toolbar 내에 있는 view들의 색상
        options.setCircleDimmedLayer(true) // crop shape
        options.setToolbarTitle("Edit Profile") //title 지정
        options.setShowCropGrid(false) // 격자표시
        options.setShowCropFrame(false) //crop의 사각형 틀 유무

        val savingUri = Uri.fromFile(tempFile)
        photoUri?.let {
            UCrop.of(it, savingUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(
                    GlobalApplication.device_width,
                    GlobalApplication.device_height
                )
                .withOptions(options)
                .start(this)
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // 이미지 파일 이름 ( JeokSyeo_{시간}_ )
        val timeStamp = SimpleDateFormat("HHmmss").format(Date())
        val imageFileName = "JeokSyeo_" + timeStamp + "_"

        // 이미지가 저장될 폴더 이름 ( JeokSyeo_ )
        val storageDir = File(getExternalFilesDir(null).toString() + "/JeokSyeo/")
        if (!storageDir.exists()) storageDir.mkdirs()

        // 빈 파일 생성
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        tempFile = image.absoluteFile
        Log.e("파일생성",tempFile?.name.toString())
        return image
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.basicHeader_backButton -> {
                finish()
                overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
            }

            R.id.editProfile_G_album -> CameraPermission()

            R.id.editProfile_G_imageButton_gender_man -> setGender_Man()

            R.id.editProfile_G_imageButton_gender_woman -> setGender_Woman()

            R.id.birthdayLinearLayout ->binding.editProfileBasicDatePicker.ExpandableDatePicker.toggle()
            R.id.button_datePicker_ok -> binding.editProfileBasicDatePicker.ExpandableDatePicker.toggle()

            R.id.editProfile_G_okButton -> presenter.executeEditProfile(this,
                binding.insertInfoEditText.text.toString(),gender,birthday)
        }
    }

    override fun checkOkButton(nicknameDuplicate:Boolean,profileCheck:Boolean) {
        if(!profileCheck){ //프로필 여부와 상관없을때
            binding.editProfileGOkButton.isEnabled = (!nicknameDuplicate &&
                    GlobalApplication.userInfo.nickName !=binding.insertInfoEditText.text.toString()) ||
                    GlobalApplication.userInfo.birthDay != birthday ||
                    GlobalApplication.userInfo.gender != gender
        }
        else{// 프로필을 셋팅했을 때
            binding.editProfileGOkButton.isEnabled =true
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
        GlobalApplication.userInfo.birthDay?.let {
            val birth = it.split("-")
            binding.birthdayYear.text = birth.get(0)
            binding.birthdayMonth.text = birth.get(1)
            binding.birthdayDay.text = birth.get(2)
        }
        birthday = GlobalApplication.userInfo.birthDay
    }

    override fun resultNickNameCheck(result: Boolean) {
        if(result){
            binding.checkNickNameText.visibility = View.VISIBLE
            binding.checkNickNameText.text = getString(R.string.dontUseNickName)
            binding.insertNameLinearLayout.background =
                resources.getDrawable(R.drawable.bottom_line_red, null)
            binding.checkNickNameText.setTextColor(resources.getColor(R.color.red, null))
        }
        else{
            binding.checkNickNameText.visibility = View.VISIBLE
            binding.checkNickNameText.text = getString(R.string.useNickName)
            binding.insertNameLinearLayout.background = resources.getDrawable(R.drawable.bottom_line_green, null)
            binding.checkNickNameText.setTextColor(resources.getColor(R.color.green, null))
        }
    }

    override fun getView(): EditProfileBinding {
        return binding
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            GlobalApplication.instance.keyPadSetting(binding.insertInfoEditText,this)
            return true
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        presenter.checkNickName(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }



    @SuppressLint("SetTextI18n")
    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        birthday = year.toString()

        binding.birthdayYear.text = year.toString()

        if (monthOfYear + 1 < 10) binding.birthdayMonth.text = "0${monthOfYear + 1}"
        else binding.birthdayMonth.text = (monthOfYear + 1).toString()

        if (dayOfMonth < 10) binding.birthdayDay.setText("0$dayOfMonth")
        else binding.birthdayDay.text = dayOfMonth.toString()

        birthday += "-" + binding.birthdayMonth.text + "-" + binding.birthdayDay.text
        checkOkButton()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }
}