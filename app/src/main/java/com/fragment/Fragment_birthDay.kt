package com.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupBirthdayBinding

class Fragment_birthDay : Fragment(), DatePicker.OnDateChangedListener, View.OnClickListener {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupBirthdayBinding

    companion object {
        fun newInstance(): Fragment_birthDay {
            val fragment = Fragment_birthDay()

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_birthday, container, false)
        binding.lifecycleOwner =this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.birthdayLinearLayout.setOnClickListener(this)
        binding.dateConfirm.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.datePicker.setOnDateChangedListener(this)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var birthDay:String = year.toString()
        binding.birthdayYear.text =year.toString()

        if (monthOfYear + 1 < 10) binding.birthdayMonth.setText("0" + (monthOfYear + 1).toString())
        else binding.birthdayMonth.setText((monthOfYear + 1).toString())

        if (dayOfMonth < 10) binding.birthdayDay.setText("0$dayOfMonth")
        else binding.birthdayDay.setText(dayOfMonth.toString())

        birthDay += "-"+ binding.birthdayMonth.text + "-" + binding.birthdayDay.text

        GlobalApplication.userBuilder.setBirthDay(birthDay)

        viewmodel.buttonState.value=true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.dateConfirm ->{
                if(binding.signUpExpandableLayout.isExpanded){
                    binding.signUpExpandableLayout.collapse()
                    viewmodel.buttonState.value=true
                }
            }

            R.id.birthdayLinearLayout ->{
                if(binding.signUpExpandableLayout.isExpanded)
                    binding.signUpExpandableLayout.collapse()
                else
                    binding.signUpExpandableLayout.expand()
            }
        }
    }
}