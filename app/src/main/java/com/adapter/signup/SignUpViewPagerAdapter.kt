package com.adapter.signup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.application.GlobalApplication
import com.fragment.login.Fragment_birthDay
import com.fragment.login.Fragment_gender
import com.fragment.login.Fragment_nickName
import com.fragment.login.RequestFragment
import com.fragment.login.location.Fragment_location

class SignUpViewPagerAdapter(
    val fragmentActivity: FragmentActivity,
    val fgList: MutableList<String>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fgList.size

    override fun createFragment(position: Int): Fragment {
        when (fgList.get(position)) {
            GlobalApplication.NICKNAME -> {
                return Fragment_nickName.newInstance()
            }
            GlobalApplication.BIRTHDAY -> {
                return Fragment_birthDay.newInstance()
            }
            GlobalApplication.GENDER -> {
                return Fragment_gender.newInstance()
            }
            GlobalApplication.LOCATION -> {
                return Fragment_location.newInstance()
            }
            else -> {
                return RequestFragment.newInstance()
            }
        }
    }

    fun getFragment(position: Int) =
        fragmentActivity.supportFragmentManager.findFragmentByTag("f$position")
}