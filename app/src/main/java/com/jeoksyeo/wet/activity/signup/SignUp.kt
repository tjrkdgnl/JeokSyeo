package com.jeoksyeo.wet.activity.signup

import adapter.SignUpViewPagerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jeoksyeo.wet.activity.application.GlobalApplication
import com.jeoksyeo.wet.activity.main.MainActivity
import com.jeoksyeo.wet.activity.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AcSignupBinding
import fragment.Fg_SignUp

class SignUp : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: AcSignupBinding
    private lateinit var mutableList: MutableList<String>
    private var idx = 0
    private lateinit var signUpViewPagerAdapter: SignUpViewPagerAdapter
    private lateinit var viewModel:SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.ac_signup)
        binding.lifecycleOwner=this

        mutableList = mutableListOf()
        mutableList.add("nickName")
        mutableList.add("birthDay")
        mutableList.add("gender")

        signUpViewPagerAdapter = SignUpViewPagerAdapter(this, mutableList)
        binding.viewPager2.adapter = signUpViewPagerAdapter
        binding.viewPager2.isUserInputEnabled =false
        binding.viewPager2.offscreenPageLimit =1
        binding.infoConfirmButton.setOnClickListener(this)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        viewModel.getButtonState().observe(this, Observer {
            binding.infoConfirmButton.isEnabled =it
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.infoConfirmButton -> updateView()
            else -> {
            }
        }
    }

     fun updateView() {
        if (idx < mutableList.size) {
            binding.viewPager2.currentItem = ++idx
            val fg = signUpViewPagerAdapter.getFragment(binding.viewPager2.currentItem)
            if (fg is Fg_SignUp){
                when(fg.info){
                    "email" -> GlobalApplication.userInfo.email =fg.item
                    "birthDay" -> GlobalApplication.userInfo.email =fg.item
                    "gender" -> GlobalApplication.userInfo.email =fg.item
                    else->{}
                }
                if(idx== mutableList.size){
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                viewModel.setButtonState(false)
            }

        }
    }
}