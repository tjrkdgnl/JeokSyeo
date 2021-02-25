package com.adapters.favorite

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fragments.favorite.Fragment_favorite

class FavoriteViewPagerAdapter(val fragmentActivity:FragmentActivity) :FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {
        return Fragment_favorite.newInstance(position)
    }
}