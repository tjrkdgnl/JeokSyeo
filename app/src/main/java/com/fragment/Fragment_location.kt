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
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupBirthdayBinding
import com.vuforia.engine.wet.databinding.FragmentSignupLocationBinding
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding

class Fragment_location : Fragment(),View.OnClickListener {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupLocationBinding
    var item: String = ""
    private var check =false

    companion object {
        fun newInstance(): Fragment_location {
            val fragment = Fragment_location()

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_location, container, false)
        binding.lifecycleOwner =this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)


        return binding.root
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}