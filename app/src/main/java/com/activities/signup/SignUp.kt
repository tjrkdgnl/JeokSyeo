package com.activities.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.base.BaseActivity
import com.activities.login.Login
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ActivitySignupBinding

class SignUp : BaseActivity<ActivitySignupBinding>(), View.OnClickListener, SignUpContract.SignUpView {
    private var idx = 0
    private lateinit var viewModel: SignUpViewModel
    private lateinit var presenter: SignUpPresenter

    override val layoutResID: Int = R.layout.activity_signup

    override fun setOnCreate() {
        presenter = SignUpPresenter().apply {
            view = this@SignUp
            activity = this@SignUp
            intent = this@SignUp.intent
        }

        presenter.initViewpager()

        binding.infoConfirmButton.setOnClickListener(this)

        setStatusBarInit()

        //확인 enable setting
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        viewModel.buttonState.observe(this, Observer {
            binding.infoConfirmButton.isEnabled = it
        })
    }

    override fun destroyPresenter() {
        presenter.detachView()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.infoConfirmButton -> nextView()

            else -> {
            }
        }
    }

    override fun getBindingObj(): ActivitySignupBinding {
        return binding
    }

    override fun setStatusBarInit() {
        //status bar 배경변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.white,null)
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            }
        }

        //status bar의 icon 색상 변경
        val decor = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or decor.systemUiVisibility
        }else{
            decor.systemUiVisibility =0
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
}