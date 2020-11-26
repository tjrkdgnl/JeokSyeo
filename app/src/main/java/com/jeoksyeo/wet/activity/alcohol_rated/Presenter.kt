package com.jeoksyeo.wet.activity.alcohol_rated

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.adapter.alcohol_rated.RatedViewPagerAdapter
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayoutMediator
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter :AlcoholRatedContact.BasesPresenter {
    override lateinit var view: AlcoholRatedContact.BaseView
    private val compositeDisposable = CompositeDisposable()

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
            TabLayoutMediator(view.getView().ratedTablayout,view.getView().ratedViewPager2,
                TabLayoutMediator.TabConfigurationStrategy{ tab, position ->
                    val textView = TextView(context)
                    tab.customView =textView
                    textView.text = lst[position]
                    textView.textSize =15f
                    textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    textView.setTextColor(context.resources.getColor(R.color.tabColor,null))
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                }).attach()

        }
    }
}