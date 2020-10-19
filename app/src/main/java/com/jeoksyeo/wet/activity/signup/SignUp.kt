package com.jeoksyeo.wet.activity.signup

import adapter.SignUpViewPagerAdapter
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ActivitySignupBinding

class SignUp : AppCompatActivity(), View.OnClickListener  {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var mutableList: MutableList<String>
    private var idx = 0
    private lateinit var signUpViewPagerAdapter: SignUpViewPagerAdapter
    private lateinit var viewModel:SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner=this

        mutableList = mutableListOf()
        intent?.let {
            if(!it.getBooleanExtra(GlobalApplication.NICKNAME,false)) mutableList.add(GlobalApplication.NICKNAME)

            if(!it.getBooleanExtra(GlobalApplication.BIRTHDAY,false)) mutableList.add(GlobalApplication.BIRTHDAY)

            if(!it.getBooleanExtra(GlobalApplication.GENDER,false)) mutableList.add(GlobalApplication.GENDER)
        }
//        mutableList.add("location")
        mutableList.add("congratulation")

        binding.signupHeader.signUpHeaderProgressbar.max = mutableList.size
        signUpViewPagerAdapter = SignUpViewPagerAdapter(this, mutableList)
        binding.viewPager2.adapter = signUpViewPagerAdapter
        binding.viewPager2.isUserInputEnabled =false //viewpager2 스와이프off
        binding.viewPager2.offscreenPageLimit =1

        binding.infoConfirmButton.setOnClickListener(this)
        binding.signupHeader.signupHeaderBackButton.setOnClickListener(this)

        //확인 enable setting
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        viewModel.getButtonState().observe(this, Observer {
            binding.infoConfirmButton.isEnabled =it
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.infoConfirmButton -> nextView()

            R.id.signup_header_backButton -> beforeView()

            else -> {
            }
        }
    }

     fun nextView() {
        if (idx < mutableList.size) {
            binding.viewPager2.currentItem = ++idx
            binding.signupHeader.signUpHeaderProgressbar.progress = idx +1
            viewModel.setButtonState(false)
            hideKeypad(binding.infoConfirmButton)
        }

    }

    fun beforeView(){
        if(idx >0){
            binding.viewPager2.currentItem = --idx
            binding.signupHeader.signUpHeaderProgressbar.progress =idx +1
            viewModel.setButtonState(true)
        }
    }

    //확인버튼 누르면 키보드 창 내리기
    fun hideKeypad(buttonName: Button) {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(buttonName.windowToken, 0)
    }
}