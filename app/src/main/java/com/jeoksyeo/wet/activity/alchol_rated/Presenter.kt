package com.jeoksyeo.wet.activity.alchol_rated

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.adapter.alchol_category.GridViewPagerAdapter
import com.adapter.alchol_rated.RatedViewPagerAdapter
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayoutMediator
import com.vuforia.engine.wet.R

class Presenter :AlcholRatedContact.BasesPresenter {
    override lateinit var view: AlcholRatedContact.BaseView

    @SuppressLint("SetTextI18n")
    override fun initProfile(provider:String?) {
        provider?.let {
            view.getView().alcholRatedName.text = GlobalApplication.userInfo.getNickName() +"님,"

        }
    }

    override fun inintTabLayout(context: Context) {
        if(context is FragmentActivity){
            view.getView().ratedViewPager2.adapter = RatedViewPagerAdapter(context)
            val lst = listOf<String>("전체","전통주","맥주","와인","양주","사케")
            TabLayoutMediator(view.getView().ratedTablayout,view.getView().ratedViewPager2,
                TabLayoutMediator.TabConfigurationStrategy{ tab, position ->
                    val textView = TextView(context)
                    tab.customView =textView
                    textView.text = lst[position]
                    textView.textSize =15f
                    textView.scaleX =1f
                    textView.scaleY =1f
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    textView.setTextColor(context.resources.getColor(R.color.tabColor,null))
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                }).attach()
        }
    }
}