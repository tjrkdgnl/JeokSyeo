package com.fragments.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupRequestBinding

class RequestFragment: BaseFragment<FragmentSignupRequestBinding>() {
    override val layoutResID: Int = R.layout.fragment_signup_request

    companion object{

        fun newInstance():Fragment{
            val fragment = RequestFragment()
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        viewmodel.checkRequest =true
        viewmodel.buttonState.value =true
        binding.basicHeader.signUpHeaderProgressbar.progress=0
    }
}