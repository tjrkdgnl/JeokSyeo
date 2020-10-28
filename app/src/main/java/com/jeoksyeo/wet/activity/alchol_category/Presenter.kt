package com.jeoksyeo.wet.activity.alchol_category

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.adapter.alchol_category.GridViewPagerAdapter
import com.adapter.alchol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.error.ErrorManager
import com.google.android.material.tabs.TabLayoutMediator
import com.model.alchol_category.AlcholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter:AlcholCategoryContact.BasePresenter {
    override lateinit var view: AlcholCategoryContact.BaseView

    override fun inintTabLayout(context: Context) {
        if(context is FragmentActivity){
            view.getView().viewPager2Container.adapter = GridViewPagerAdapter(context)
            val lst = listOf<String>("전통주","맥주","와인","양주","사케")
            TabLayoutMediator(view.getView().tabLayoutAlcholList,view.getView().viewPager2Container,
                TabLayoutMediator.TabConfigurationStrategy{ tab, position ->
                    val textView = TextView(context)
                    tab.customView =textView
                    textView.text = lst[position]
                    textView.textSize =15f
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    textView.setTextColor(context.resources.getColor(R.color.black,null))
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                }).attach()
        }
    }

    override fun getFragement(position: Int): Fragment? {
        var fragment:Fragment? =null
        if(view.getView().viewPager2Container.adapter is GridViewPagerAdapter){
            fragment= (view.getView().viewPager2Container.adapter as GridViewPagerAdapter)
                .getFragment(view.getView().viewPager2Container.currentItem)
        }
        else if (view.getView().viewPager2Container.adapter is ListViewPagerAdapter){
            fragment= (view.getView().viewPager2Container.adapter as ListViewPagerAdapter)
                .getFragment(view.getView().viewPager2Container.currentItem)
        }

        return fragment
    }

}