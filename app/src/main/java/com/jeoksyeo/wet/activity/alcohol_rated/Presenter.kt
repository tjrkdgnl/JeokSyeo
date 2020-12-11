package com.jeoksyeo.wet.activity.alcohol_rated

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.adapter.alcohol_rated.RatedViewPagerAdapter
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayoutMediator
import com.service.NetworkUtil
import com.vuforia.engine.wet.R
import io.reactivex.disposables.CompositeDisposable

class Presenter :AlcoholRatedContact.BasesPresenter {
    override lateinit var view: AlcoholRatedContact.BaseView
    override lateinit var context: Context

    private lateinit var networkUtil:NetworkUtil


    override fun setNetworkUtil() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(context)
            networkUtil.register()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initProfile(provider:String?) {
        provider?.let {
            view.getView().profileHeader.alcoholRatedName.text = GlobalApplication.userInfo.nickName +"님,"
        }
    }

    override fun initTabLayout(context: Context) {
        if(context is FragmentActivity){
            view.getView().ratedViewPager2.adapter = RatedViewPagerAdapter(context)
            val lst = listOf<String>("전체","전통주","맥주","와인","양주","사케")
            TabLayoutMediator(view.getView().ratedTablayout,view.getView().ratedViewPager2
            ) { tab, position ->
                val textView = TextView(context)
                tab.customView = textView
                textView.text = lst[position]
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.tab_text_size)
                )
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
}