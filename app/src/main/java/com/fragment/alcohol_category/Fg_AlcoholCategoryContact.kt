package com.fragment.alcohol_category

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.model.alcohol_category.AlcoholList

interface Fg_AlcoholCategoryContact {

    interface BaseView{
        fun getbinding(): ViewDataBinding
        fun setAdapter(list:MutableList<AlcoholList>)
        fun updateList(list:MutableList<AlcoholList>)
        fun changeSort(list:MutableList<AlcoholList>)
        fun getLastAlcoholId():String?
        fun getSort():String
    }
    interface BasePresenter{
        var view:BaseView
        var context:Context

        fun initRecyclerView(context: Context)

        fun initScrollListener()

        fun pagination(alcoholId:String?)

        fun changeSort(sort: String)

        fun setSortValue(sort:String)

    }
}