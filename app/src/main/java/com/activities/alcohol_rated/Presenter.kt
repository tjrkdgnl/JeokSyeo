package com.activities.alcohol_rated

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.adapters.alcohol_rated.RatedViewPagerAdapter
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayoutMediator
import com.vuforia.engine.wet.R

class Presenter :AlcoholRatedContact.RatedPresenter {
    override var viewObj: AlcoholRatedContact.RatedView? =null
    override  val view: AlcoholRatedContact.RatedView by lazy {
        viewObj!!
    }

    override lateinit var activity: Activity

    @SuppressLint("SetTextI18n")
    override fun initProfile(provider:String?) {
        provider?.let {

            view.getBindingObj().profileHeader.alcoholRatedName.text = GlobalApplication.userInfo.nickName +"님,"
        }
    }

    override fun initTabLayout(context: Context) {
        if(context is FragmentActivity){
            view.getBindingObj().ratedViewPager2.adapter = RatedViewPagerAdapter(context)
            val lst = listOf<String>("전체","전통주","맥주","와인","양주","사케")
            TabLayoutMediator(view.getBindingObj().ratedTablayout,view.getBindingObj().ratedViewPager2
            ) { tab, position ->
                val textView = TextView(context)
                tab.customView = textView
                textView.text = lst[position]
                //(폰트 고정 사이즈 * textview의 고정 넓이) * 비율로 계산된 값
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, GlobalApplication.instance.getCalculatorTextSize(16f,true,6))

                textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                textView.gravity = Gravity.CENTER_HORIZONTAL

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (tab.customView as? TextView)?.setTextColor(context.resources.getColor(R.color.tabColor,null))
                } else {
                    (tab.customView as? TextView)?.setTextColor(ContextCompat.getColor(context,R.color.tabColor))
                }

            }.attach()

        }
    }

    override fun detach() {
        viewObj =null
    }
}