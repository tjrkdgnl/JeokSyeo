package com.fragments.signup

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.base.BaseFragment
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupGenderBinding

class Fragment_gender : BaseFragment<FragmentSignupGenderBinding>(),View.OnClickListener{
    override val layoutResID: Int =  R.layout.fragment_signup_gender
    private lateinit var viewmodel: SignUpViewModel
    private var maleCheck = false
    private var femaleCheck = false

    companion object {
        fun newInstance(): Fragment_gender {
            val fragment = Fragment_gender()
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.maleLinearLayout.setOnClickListener(this)
        binding.femaleLinearLayout.setOnClickListener(this)
    }

    override fun detachPresenter() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.male_LinearLayout ->{checkMale()}

            R.id.female_LinearLayout ->{checkFemale()}

            else->{}
        }
    }

    //남성을 선택한 경우 여성 셋팅 해제
    private fun checkMale(){
        if(!maleCheck){
            binding.maleCheckbox.setImageResource(R.mipmap.gender_checkbox_full)
            maleCheck=true

            binding.femaleCheckbox.setImageResource(R.mipmap.gender_checkbox_empty)
            femaleCheck=false

            GlobalApplication.userBuilder.setGender("M")
        }

        //ok버튼 활성화
        viewmodel.buttonState.value=true
    }

    //여성을 선택한 경우 남성 셋팅 해제
    private fun checkFemale(){
        if(!femaleCheck){
            binding.femaleCheckbox.setImageResource(R.mipmap.gender_checkbox_full)
            femaleCheck=true

            binding.maleCheckbox.setImageResource(R.mipmap.gender_checkbox_empty)
            maleCheck=false

            GlobalApplication.userBuilder.setGender("F")
        }
        //ok버튼 활성화
        viewmodel.buttonState.value=true
    }
}