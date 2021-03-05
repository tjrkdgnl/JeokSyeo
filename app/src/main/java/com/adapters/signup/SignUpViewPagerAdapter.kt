package com.adapters.signup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.application.GlobalApplication
import com.fragments.signup.Fragment_birthDay
import com.fragments.signup.Fragment_gender
import com.fragments.signup.Fragment_nickName
import com.fragments.signup.location.Fragment_location
import java.lang.Exception

class SignUpViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    val fgList: MutableList<String>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fgList.size

    override fun createFragment(position: Int): Fragment {
        return when (fgList.get(position)) {
            GlobalApplication.NICKNAME -> {
                Fragment_nickName.newInstance()
            }
            GlobalApplication.BIRTHDAY -> {
                Fragment_birthDay.newInstance()
            }
            GlobalApplication.GENDER -> {
                Fragment_gender.newInstance()
            }
            GlobalApplication.LOCATION -> {
                Fragment_location.newInstance()
            }
            else -> {
                throw Exception("회원가입 뷰페이저 에러 ")
            }
        }
    }
}