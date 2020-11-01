package com.fragment.login

import android.annotation.SuppressLint
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
import java.util.*

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup_birthday, container, false)
        binding.lifecycleOwner = this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.basicDatePicker.datePicker.maxDate = Calendar.getInstance().time.time

        binding.birthdayLinearLayout.setOnClickListener(this)
        binding.basicDatePicker.buttonDatePickerOk.setOnClickListener(this)

        val calendar = Calendar.getInstance()
        binding.basicDatePicker.datePicker.maxDate = calendar.time.time
        binding.basicDatePicker.datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), this
        )
        binding.birthdayYear.text = binding.basicDatePicker.datePicker.year.toString()
        binding.birthdayMonth.text = (binding.basicDatePicker.datePicker.month +1).toString()
        binding.birthdayDay.text = binding.basicDatePicker.datePicker.dayOfMonth.toString()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var birthDay: String = year.toString()
        binding.birthdayYear.text = year.toString()

        if (monthOfYear + 1 < 10) binding.birthdayMonth.setText("0" + (monthOfYear + 1).toString())
        else binding.birthdayMonth.setText((monthOfYear + 1).toString())

        if (dayOfMonth < 10) binding.birthdayDay.setText("0$dayOfMonth")
        else binding.birthdayDay.setText(dayOfMonth.toString())

        birthDay += "-" + binding.birthdayMonth.text + "-" + binding.birthdayDay.text

        GlobalApplication.userBuilder.setBirthDay(birthDay)

        viewmodel.buttonState.value = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.birthdayLinearLayout -> {
                binding.basicDatePicker.ExpandableDatePicker.toggle()
            }

            R.id.button_datePicker_ok -> {
                if (binding.basicDatePicker.ExpandableDatePicker.isExpanded) {
                    binding.basicDatePicker.ExpandableDatePicker.collapse()
                    viewmodel.buttonState.value = true
                }
            }
        }
    }
}