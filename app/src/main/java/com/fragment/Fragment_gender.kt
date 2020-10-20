package com.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupGenderBinding
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding

class Fragment_gender : Fragment(),View.OnClickListener{
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupGenderBinding
    private var maleCheck = false
    private var femaleCheck = false

    companion object {
        fun newInstance(): Fragment_gender {
            val fragment = Fragment_gender()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_gender, container, false)
        binding.lifecycleOwner =this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.maleCheckbox.setOnClickListener(this)
        binding.femaleCheckbox.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.male_checkbox ->{checkMale()}

            R.id.female_checkbox ->{checkFemale()}

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

        viewmodel.setButtonState(true)
    }

    private fun checkFemale(){
        if(!femaleCheck){
            binding.femaleCheckbox.setImageResource(R.mipmap.gender_checkbox_full)
            femaleCheck=true

            binding.maleCheckbox.setImageResource(R.mipmap.gender_checkbox_empty)
            maleCheck=false

            GlobalApplication.userBuilder.setGender("F")
        }

        viewmodel.setButtonState(true)
    }
}