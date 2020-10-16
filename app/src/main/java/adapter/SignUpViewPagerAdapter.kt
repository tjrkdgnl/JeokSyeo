package adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import fragment.Fg_SignUp

class SignUpViewPagerAdapter(val fragmentActivity: FragmentActivity,val fgList:MutableList<String>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int =fgList.size

    override fun createFragment(position: Int): Fragment {
        return Fg_SignUp.newInstance(fgList.get(position))
    }

    fun getFragment(position: Int)= fragmentActivity.supportFragmentManager.findFragmentByTag("f$position")
}