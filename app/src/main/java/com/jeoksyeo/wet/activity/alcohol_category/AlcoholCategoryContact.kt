package com.jeoksyeo.wet.activity.alcohol_category

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
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