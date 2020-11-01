package com.adapter.alchol_rated

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fragment.alchol_rated.Fragment_alcholRated
import com.jeoksyeo.wet.activity.alchol_rated.AlcholRated

class RatedViewPagerAdapter(val activity:FragmentActivity) :FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {
        return Fragment_alcholRated.newInstance(position)
    }
}