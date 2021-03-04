package com.activities.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.activities.login.Login
import com.application.GlobalApplication
import com.base.BaseActivity
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
            viewObj = this@SignUp
            activity = this@SignUp
            intent = this@SignUp.intent
        }

        presenter.initViewpager()

        binding.infoConfirmButton.setOnClickListener(this)

        setStatusBarInit()

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        //유저가 정보를 기입한 경우 '확인'버튼 활성화 및 비활성화 설정
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
        //horizontal viewpager로 '확인'버튼 클릭 시, 다음 페이지로 이동
        binding.viewPager2.currentItem = ++idx

        //프로그래스바로 진행상태 표시
        binding.signupHeader.signUpHeaderProgressbar.progress = idx + 1

        //키패드 숨기기
        presenter.hideKeypad(binding.infoConfirmButton)

        if (viewModel.checkRequest) { //에러가 발생하면 핸들링하는 화면
            startActivity(Intent(this, Login::class.java))
            finish()
        } else if (viewModel.nickname != null) {
            viewModel.nickname?.let { nic ->
                GlobalApplication.userBuilder.setNickName(nic)
                viewModel.nickname = null       //해당 부분을 반드시 null처리해야 유저 정보가 제대로 기입된다.
            }
        }
        else if (viewModel.birthDay !=null){
            viewModel.birthDay?.let { birth->
                GlobalApplication.userBuilder.setBirthDay(birth)
                viewModel.birthDay =null         //해당 부분을 반드시 null처리해야 유저 정보가 제대로 기입된다.
            }
        }
        else if (viewModel.gender !=null){
            viewModel.gender?.let { gen ->
                GlobalApplication.userBuilder.setGender(gen)
                viewModel.gender =null           //해당 부분을 반드시 null처리해야 유저 정보가 제대로 기입된다.
            }
        }
        else if (viewModel.OkButtonEnabled) { //지역선택
            //첫번째 depth는 가장 큰 범주인 지역이다. 해당 지역의 코드가 저장이 되며, 이 코드를 통해서
            //두번째 depth에서는 해당 지역의 작은 범주의 지역들이 나타난다.
            if (viewModel.depth == 1) {
                GlobalApplication.userBuilder.setAddress(
                    viewModel.countryArea.value?.code ?: ""
                )
            } else if (viewModel.depth == 2) {
                GlobalApplication.userBuilder.setAddress(
                    viewModel.townArea.value?.code ?: ""
                )
            }

            //위치까지 모두 설정했으면 유저의 모든 정보를 api로 전송하여 회원가입 신청
            presenter.signUp()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}