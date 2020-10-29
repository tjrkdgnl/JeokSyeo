package com.fragment.alchol_category

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.model.alchol_category.AlcholList
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding

interface Fg_AlcholCategoryContact {

    interface BaseView{
        fun getbinding(): ViewDataBinding
        fun setAdapter(list:MutableList<AlcholList>)
        fun updateList(list:MutableList<AlcholList>)
        fun setAlcholTotalCount(totalCount:Int)
        fun changeSort(list:MutableList<AlcholList>)
        fun getLastAlcholId():String?
        fun getSort():String
    }

    interface BasePresenter{
        var view:BaseView
        fun initRecyclerView(context: Context, lastAlcholId:String?)

        fun initScrollListener()

        fun pagination(alcholId:String?)

        fun changeSort(sort: String)

        fun setSortValue(sort:String)

    }

}