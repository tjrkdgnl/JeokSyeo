package com.jeoksyeo.wet.activity.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.login.Login
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ActivitySignupBinding

class SignUp : AppCompatActivity(), View.OnClickListener, SignUpContract.BaseView {
    private lateinit var binding: ActivitySignupBinding
    private var idx = 0
    private lateinit var viewModel: SignUpViewModel
    private lateinit var presenter: SignUpPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner = this

        presenter = SignUpPresenter().apply {
            view = this@SignUp
            activity = this@SignUp
            intent = this@SignUp.intent
        }
        presenter.setNetworkUtil()

        presenter.initViewpager()

        binding.infoConfirmButton.setOnClickListener(this)
        binding.signupHeader.signupHeaderBackButton.setOnClickListener(this)

        //확인 enable setting
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        viewModel.buttonState.observe(this, Observer {
            binding.infoConfirmButton.isEnabled = it
        })
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = SignUp::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
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

    override fun getBinding(): ActivitySignupBinding {
        return binding
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
        else if (viewModel.OkButtonEnabled) { //지역선택
            if (viewModel.depth == 1) {
                GlobalApplication.userBuilder.setAddress(
                    viewModel.countryArea.value?.code ?: ""
                )
            } else if (viewModel.depth == 2) {
                GlobalApplication.userBuilder.setAddress(
                    viewModel.townArea.value?.code ?: ""
                )
            }

            presenter.signUp()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}