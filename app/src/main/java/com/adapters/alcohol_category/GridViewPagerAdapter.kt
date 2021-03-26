package com.adapters.alcohol_category

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.application.GlobalApplication
import com.fragments.alcohol_category.viewpager_items.Fragment_Grid

class GridViewPagerAdapter(val fragment: Fragment):FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return GlobalApplication.CATEGORY_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        return Fragment_Grid.newInstance(position)
    }

    fun getFragment(position:Int):Fragment? {
        return fragment.childFragmentManager.findFragmentByTag("f$position")
    }
}