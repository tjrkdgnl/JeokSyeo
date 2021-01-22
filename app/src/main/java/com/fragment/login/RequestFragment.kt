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
    private  var bindObj:FragmentSignupRequestBinding? =null

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
        bindObj = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_nickname, container, false)
        binding = bindObj!!
        val viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        viewmodel.checkRequest =true
        viewmodel.buttonState.value =true
        binding.basicHeader.signUpHeaderProgressbar.progress=0


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        bindObj = null
    }
}