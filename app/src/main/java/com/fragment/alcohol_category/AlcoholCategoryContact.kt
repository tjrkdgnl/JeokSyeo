package com.fragment.alcohol_category

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding

interface AlcoholCategoryContact {

    interface BaseView{
       fun getViewBinding():AlcoholCategoryBinding

        fun changeToggle(toggle:Boolean)

        fun setTotalCount(alcoholCount:Int)

    }

    interface BasePresenter{
        var view: BaseView
        var context:Context

        fun inintTabLayout(context: Context,currentItem:Int)

        fun getFragement(position:Int): Fragment?

        fun checkSort(position:Int,sort:String)

        fun initNavigationItemSet(context: Context,activity:Activity)

        fun checkLogin(context: Context)


        fun detach()

    }
}