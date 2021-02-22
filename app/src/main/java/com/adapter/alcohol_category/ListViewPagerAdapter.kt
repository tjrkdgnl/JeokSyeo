package com.adapter.alcohol_category

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.application.GlobalApplication
import com.fragment.alcohol_category.viewpager_items.Fragment_List

class ListViewPagerAdapter(val fragment: Fragment):FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return GlobalApplication.CATEGORY_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        return Fragment_List.newInstance(position)
    }

    fun getFragment(position:Int):Fragment? {
        return fragment.childFragmentManager.findFragmentByTag("f$position")
    }
}