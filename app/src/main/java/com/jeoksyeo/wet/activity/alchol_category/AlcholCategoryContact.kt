package com.jeoksyeo.wet.activity.alchol_category

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.model.alchol_category.AlcholList
import com.model.alchol_detail.Alchol
import com.vuforia.engine.wet.databinding.AlcholCategoryBinding

interface AlcholCategoryContact {

    interface BaseView{
       fun getView():AlcholCategoryBinding

        fun changeToggle(toggle:Boolean)

        fun setTotalCount(alcholCount:Int)

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