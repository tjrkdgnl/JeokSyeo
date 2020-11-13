package com.adapter.search

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.NoResentViewholder
import com.adapter.viewholder.SearchViewHolder
import com.jeoksyeo.wet.activity.search.SearchContract
import java.lang.RuntimeException

class SearchAdapter(
    val context: Context,
    var keywordLst: MutableList<String>,
    private val searchInterface: SearchContract.BaseVIew
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val NO_SEARCH = -1
    private val MY_SEARCH = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            -1 -> {NoResentViewholder(parent)}

            1 ->{SearchViewHolder(parent, searchInterface)}

            else -> {throw RuntimeException("알 수 없는 뷰타입에러")}
        }
    }

    override fun getItemCount(): Int {
        return keywordLst.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SearchViewHolder){
            holder.bind(keywordLst[position])
        }

    }

    fun updateList(list: MutableList<String>) {
        keywordLst.clear()
        keywordLst.addAll(list.toMutableList())
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if(keywordLst[position] =="-1"){
            NO_SEARCH
        } else
            MY_SEARCH
    }
}