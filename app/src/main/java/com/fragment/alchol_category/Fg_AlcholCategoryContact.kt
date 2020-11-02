package com.fragment.alchol_category

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.model.alchol_category.AlcholList

interface Fg_AlcholCategoryContact {

    interface BaseView{
        fun getbinding(): ViewDataBinding
        fun setAdapter(list:MutableList<AlcholList>)
        fun updateList(list:MutableList<AlcholList>)
        fun changeSort(list:MutableList<AlcholList>)
        fun getLastAlcholId():String?
        fun getSort():String
        fun setTotalCount(alcholCount:Int)
    }
    interface BasePresenter{
        var view:BaseView
        fun initRecyclerView(context: Context)

        fun initScrollListener()

        fun pagination(alcholId:String?)

        fun changeSort(sort: String)

        fun setSortValue(sort:String)

    }
}