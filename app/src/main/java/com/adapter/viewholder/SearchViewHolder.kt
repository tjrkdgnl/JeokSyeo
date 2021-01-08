package com.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.fragment.search.SearchContract
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchItemRelativeBinding

class SearchViewHolder(parent: ViewGroup, val searchInterface: SearchContract.BaseVIew) :
    BaseViewHolder<String, SearchItemRelativeBinding>(R.layout.search_item_relative, parent) {

    override fun bind(data: String) {
        getViewBinding().item = data
        getViewBinding().executePendingBindings()




        getViewBinding().searchContentsLayout.setOnClickListener {
            searchInterface.changeAdapter(data,false) //연관 검색어 클릭 시, 검색 결과가 나오게끔 핸들링
            GlobalApplication.userDataBase.setKeyword(data)
        }
    }

    fun setImage(searchImg:Int){
        getViewBinding().imageViewSearchIemIcon.setImageResource(searchImg)
        when(searchImg){
            R.mipmap.resent_timer->{
                getViewBinding().deleteMySearch.visibility = View.VISIBLE
            }

            R.mipmap.relative_search_btn->{
                getViewBinding().deleteMySearch.visibility = View.INVISIBLE
            }
        }
    }
}