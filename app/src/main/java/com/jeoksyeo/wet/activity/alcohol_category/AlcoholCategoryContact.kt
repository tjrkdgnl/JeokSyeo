package com.jeoksyeo.wet.activity.alcohol_category

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.model.alcohol_category.AlcoholList
import com.model.alcohol_detail.Alcohol
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding

interface AlcoholCategoryContact {

    interface BaseView{
       fun getView():AlcoholCategoryBinding

        fun changeToggle(toggle:Boolean)

        fun setTotalCount(alcoholCount:Int)

    }

    interface BasePresenter{
        var view:BaseView

        fun inintTabLayout(context: Context)

        fun getFragement(position:Int): Fragment?

        fun checkSort(position:Int,sort:String)

        fun initNavigationItemSet(context: Context,activity:Activity)

        fun checkLogin(context: Context)


    }
}