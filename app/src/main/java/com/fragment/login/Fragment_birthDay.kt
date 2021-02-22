package com.fragment.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupBirthdayBinding
import java.text.SimpleDateFormat
import java.util.*

class Fragment_birthDay : BaseFragment<FragmentSignupBirthdayBinding>(), DatePicker.OnDateChangedListener, View.OnClickListener {

    override val layoutResID: Int = R.layout.fragment_signup_birthday
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var date:String

    companion object {
        fun newInstance(): Fragment_birthDay {
            val fragment = Fragment_birthDay()

            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())

        binding.basicDatePicker.datePicker.maxDate = Calendar.getInstance().time.time

        binding.birthdayLinearLayout.setOnClickListener(this)
        binding.basicDatePicker.buttonDatePickerOk.setOnClickListener(this)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR,-15)

        //최대 날짜를 통해 사용자가 생년월일이 지정했는지 판단
        date= simpleDateFormat.format(calendar.time)
        binding.basicDatePicker.datePicker.maxDate = calendar.time.time
        binding.basicDatePicker.datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), this
        )
        binding.birthdayYear.text = binding.basicDatePicker.datePicker.year.toString()
        binding.birthdayMonth.text = (binding.basicDatePicker.datePicker.month +1).toString()
        binding.birthdayDay.text = binding.basicDatePicker.datePicker.dayOfMonth.toString()

    }

    @SuppressLint("SetTextI18n")
    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var birthDay: String = year.toString()
        binding.birthdayYear.text = year.toString()

        if (monthOfYear + 1 < 10) binding.birthdayMonth.text = "0" + (monthOfYear + 1).toString()
        else binding.birthdayMonth.text = (monthOfYear + 1).toString()

        if (dayOfMonth < 10) binding.birthdayDay.setText("0$dayOfMonth")
        else binding.birthdayDay.text = dayOfMonth.toString()

        birthDay += "-" + binding.birthdayMonth.text + "-" + binding.birthdayDay.text

        viewmodel.birthDay = birthDay

        //생년월일을 변경하지 않으면 버튼 활성화가 되면 안된다.
        viewmodel.buttonState.value = birthDay != date
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.birthdayLinearLayout -> {
                binding.basicDatePicker.ExpandableDatePicker.toggle()
            }

            R.id.button_datePicker_ok -> {
                if (binding.basicDatePicker.ExpandableDatePicker.isExpanded) {
                    binding.basicDatePicker.ExpandableDatePicker.collapse()
                }
            }
        }
    }

}