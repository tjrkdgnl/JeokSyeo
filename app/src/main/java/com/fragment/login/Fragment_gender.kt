package com.fragment.login

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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.male_LinearLayout ->{checkMale()}

            R.id.female_LinearLayout ->{checkFemale()}

            else->{}
        }
    }

    private fun checkMale(){
        if(!maleCheck){
            binding.maleCheckbox.setImageResource(R.mipmap.gender_checkbox_full)
            maleCheck=true

            binding.femaleCheckbox.setImageResource(R.mipmap.gender_checkbox_empty)
            femaleCheck=false

            GlobalApplication.userBuilder.setGender("M")
        }

        viewmodel.buttonState.value=true
    }

    private fun checkFemale(){
        if(!femaleCheck){
            binding.femaleCheckbox.setImageResource(R.mipmap.gender_checkbox_full)
            femaleCheck=true

            binding.maleCheckbox.setImageResource(R.mipmap.gender_checkbox_empty)
            maleCheck=false

            GlobalApplication.userBuilder.setGender("F")
        }

        viewmodel.buttonState.value=true
    }

}