package com.jeoksyeo.wet.activity.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adapter.signup.SignUpViewPagerAdapter
import com.application.GlobalApplication
import com.google.firebase.messaging.FirebaseMessaging
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.main.MainActivity
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ActivitySignupBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUp : AppCompatActivity(), View.OnClickListener, SignUpContract.BaseView {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var mutableList: MutableList<String>
    private var idx = 0
    private lateinit var signUpViewPagerAdapter: SignUpViewPagerAdapter
    private lateinit var viewModel: SignUpViewModel
    private lateinit var presenter: SignUpPresenter
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner = this

        init()

        binding.infoConfirmButton.setOnClickListener(this)
        binding.signupHeader.signupHeaderBackButton.setOnClickListener(this)

        //확인 enable setting
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        viewModel.buttonState.observe(this, Observer {
            binding.infoConfirmButton.isEnabled = it
        })
    }

    private fun init() {
        presenter = SignUpPresenter().apply {
            view = this@SignUp
        }

        mutableList = mutableListOf()
        mutableList.add(GlobalApplication.NICKNAME)
        intent?.let {
            val bundle = it.getBundleExtra(GlobalApplication.USER_BUNDLE)
            bundle?.let { bun ->
                if (!bun.getBoolean(GlobalApplication.BIRTHDAY, false)) mutableList.add(
                    GlobalApplication.BIRTHDAY
                )
                if (!bun.getBoolean(GlobalApplication.GENDER, false)) mutableList.add(
                    GlobalApplication.GENDER
                )
            }
        }
        mutableList.add("location")

        //progressbar init
        binding.signupHeader.signUpHeaderProgressbar.max = mutableList.size

        //viewPager2 init
        signUpViewPagerAdapter = SignUpViewPagerAdapter(this, mutableList)
        binding.viewPager2.adapter = signUpViewPagerAdapter
        binding.viewPager2.isUserInputEnabled = false //viewpager2 스와이프off
        binding.viewPager2.offscreenPageLimit = 1


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.infoConfirmButton -> nextView()

            R.id.signup_header_backButton -> {
                finish()
                overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
            }

            else -> {
            }
        }
    }

    @SuppressLint("HardwareIds")
    override fun nextView() {
        viewModel.buttonState.value = false
        binding.viewPager2.currentItem = ++idx
        binding.signupHeader.signUpHeaderProgressbar.progress = idx + 1
        presenter.hideKeypad(this, binding.infoConfirmButton)

        if (viewModel.checkRequest) { //에러가 발생하면 핸들링하는 화면
            startActivity(Intent(this, Login::class.java))
            finish()
        } else if (viewModel.nickname != null) {
            viewModel.nickname?.let { nic ->
                GlobalApplication.userBuilder.setNickName(nic)
                viewModel.nickname = null
            }
        }
        else if (viewModel.birthDay !=null){
            viewModel.birthDay?.let { birth->
                GlobalApplication.userBuilder.setBirthDay(birth)
                viewModel.birthDay =null
            }
        }
        else if (viewModel.gender !=null){
            viewModel.gender?.let { gen ->
                GlobalApplication.userBuilder.setGender(gen)
                viewModel.gender =null
            }
        }
        else if (viewModel.lock) { //지역선택
            if (viewModel.depth == 1) {
                GlobalApplication.userBuilder.setAddress(
                    viewModel.countryArea.value?.code ?: ""
                )
            } else if (viewModel.depth == 2) {
                GlobalApplication.userBuilder.setAddress(
                    viewModel.townArea.value?.code ?: ""
                )
            }

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("디바이스 토큰 에러", task.exception?.message.toString())
                } else {
                    //회원정보객체 셋팅
                    GlobalApplication.userInfo = GlobalApplication.userBuilder.build()

                    val userMap = GlobalApplication.userInfo.getMap()

                    userMap["device_platform"] = "AOS"
                    userMap["device_model"] = Build.MODEL
                    userMap["device_id"] =Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                    userMap["device_token"] = task.result!!

                    disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                        .signUp(GlobalApplication.userBuilder.createUUID, userMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //내장 디비에 토큰 값들 저장
                            GlobalApplication.userDataBase.setAccessToken(it.data?.token?.accessToken)
                            GlobalApplication.userDataBase.setRefreshToken(it.data?.token?.refreshToken)

                            //토큰 안에 들어있는 만료시간 구해서 내장디비에 저장
                            JWTUtil.decodeAccessToken(it.data?.token?.accessToken.toString())
                            JWTUtil.decodeRefreshToken(it.data?.token?.refreshToken.toString())

                            GlobalApplication.instance.moveActivity(this, MainActivity::class.java,
                                Intent.FLAG_ACTIVITY_CLEAR_TOP,null,null,1)
                        }, { t: Throwable -> t.stackTrace })
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }

}