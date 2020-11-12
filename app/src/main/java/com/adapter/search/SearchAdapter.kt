package com.adapter.search

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.SearchViewHolder
import com.jeoksyeo.wet.activity.search.SearchContract
import io.reactivex.disposables.CompositeDisposable

class SearchAdapter(val context:Context, var keywordLst:MutableList<String>,private val searchInterface:SearchContract.BaseVIew) : RecyclerView.Adapter<SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(parent,searchInterface)
    }

    override fun getItemCount(): Int {
        return keywordLst.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(keywordLst[position])
    }

    fun updateList(list: MutableList<String>){
        keywordLst.clear()
        keywordLst.addAll(list.toMutableList())
        notifyDataSetChanged()
    }
}