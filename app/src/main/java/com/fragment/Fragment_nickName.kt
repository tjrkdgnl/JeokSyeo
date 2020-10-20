package com.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding

class Fragment_nickName : Fragment(), TextWatcher ,View.OnKeyListener {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupNicknameBinding
    private var check =false

    companion object {
        fun newInstance(): Fragment_nickName {
            val fragment = Fragment_nickName()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_nickname, container, false)
        binding.lifecycleOwner =this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)


        binding.insertInfoEditText.addTextChangedListener(this)
        binding.insertInfoEditText.setOnKeyListener(this)

        return binding.root
    }


    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(s.toString().isEmpty()){
            viewmodel.setButtonState(false)
            check =false
        }
        else{
            if(!check){

                viewmodel.setButtonState(true)
                check =true
            }
        }
        GlobalApplication.userBuilder.setNickName(s.toString())
    }

    fun hideKeypad(edit_nickname: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edit_nickname.windowToken, 0)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
            hideKeypad(binding.insertInfoEditText)
            return true
        }
        return false
    }
}