package com.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupRequestBinding

class RequestFragment:Fragment() {
    private  lateinit var binding:FragmentSignupRequestBinding
    companion object{

        fun newInstance():Fragment{
            val fragment = RequestFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_request,container,false)
        val viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        viewmodel.checkRequest =true
        viewmodel.buttonState.value =true
        binding.basicHeader.signUpHeaderProgressbar.progress=0


        return binding.root
    }
}