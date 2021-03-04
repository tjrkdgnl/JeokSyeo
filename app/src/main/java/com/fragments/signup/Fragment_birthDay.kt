package com.fragments.signup

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

        binding.birthdayLinearLayout.setOnClickListener(this)
        binding.basicDatePicker.buttonDatePickerOk.setOnClickListener(this)

        //최대 날짜는 현재 날짜로부터 15년 전
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR,-15)

        //스피너에 기본 날짜 설정
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

    override fun detachPresenter() {

    }

    //스피너의 날짜가 변경될 때마다 뷰모델에 값을 셋팅
    @SuppressLint("SetTextI18n")
    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var birthDay: String = year.toString()
        binding.birthdayYear.text = year.toString()

        //momth가 한자리 수라면(1월~9월) 앞에 0을 붙인다.
        //0을 붙이는 이유는 api 파라미터의 form을 맞추기 위해서다.
        if (monthOfYear + 1 < 10) binding.birthdayMonth.text = "0" + (monthOfYear + 1).toString()
        else binding.birthdayMonth.text = (monthOfYear + 1).toString()

        //day가 한자리 수라면(1일~9일) 앞에 0을 붙인다.
        if (dayOfMonth < 10) binding.birthdayDay.setText("0$dayOfMonth")
        else binding.birthdayDay.text = dayOfMonth.toString()

        //api 파라미터 형식은 YYYY-MM-DD이기 때문에 해당 form으로 변형하여 저장한다.
        birthDay += "-" + binding.birthdayMonth.text + "-" + binding.birthdayDay.text

        viewmodel.birthDay = birthDay

        //버튼 활성화를 위해서 뷰모델에 생년월일 변수를 저장한다.
        //생년월일을 변경하지 않으면 버튼 활성화가 되면 안된다.
        viewmodel.buttonState.value = birthDay != date
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.birthdayLinearLayout -> {
                //스피너 접고 펼치기
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