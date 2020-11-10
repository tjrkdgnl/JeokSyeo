package com.adapter.alcohol_rated

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fragment.alcohol_rated.Fragment_alcoholRated
import com.jeoksyeo.wet.activity.alcohol_rated.AlcoholRated

class RatedViewPagerAdapter(val activity:FragmentActivity) :FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {
        return Fragment_alcoholRated.newInstance(position)
    }
}