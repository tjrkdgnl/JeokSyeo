package com.jeoksyeo.wet.activity.search

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.search.SearchAdapter
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchBinding

interface SearchContract {

    interface BaseVIew{
        fun getView():SearchBinding

        fun initSearchAdapter():SearchAdapter
        fun updateRelativeList(list:MutableList<String>,searchImg:Int = R.mipmap.resent_timer)
        fun setSearchList(list:MutableList<AlcoholList>)
        fun updatePaging(list:MutableList<AlcoholList>)
        fun changeAdapter(keyword: String?,relativeCheck:Boolean =true)
        fun noSearchItem(check:Boolean)
    }


    interface BasePresenter{
        var view:BaseVIew
        var layoutManager:LinearLayoutManager
        var activity:Activity

        fun detach()
        fun setSearchResult(keyword:String?)

        fun setRelativeSearch(keyword:String)

        fun setNetworkUtil()
    }
}