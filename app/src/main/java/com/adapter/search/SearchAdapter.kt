package com.adapter.search

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.NoRelativeSearchViewHolder
import com.adapter.viewholder.NoResentViewholder
import com.adapter.viewholder.SearchViewHolder
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.search.SearchContract
import com.vuforia.engine.wet.R

class SearchAdapter(
    val context: Context,
    var keywordLst: MutableList<String>,
    private val searchInterface: SearchContract.BaseVIew
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val NO_SEARCH = -1
    private val MY_SEARCH = 1
    private val NO_RELATIVE=2
    private var searchImg: Int = R.mipmap.resent_timer //이미지에 따라서 delete 레이아웃 visible 여부 결정

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_SEARCH -> {
                NoResentViewholder(parent,searchInterface)
            }
            MY_SEARCH -> {
                SearchViewHolder(parent, searchInterface)
            }
            NO_RELATIVE -> {
                NoRelativeSearchViewHolder(parent)
            }
            else -> {
                throw RuntimeException("알 수 없는 뷰타입에러")
            }
        }
    }

    override fun getItemCount(): Int {
        return keywordLst.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchViewHolder) {
            holder.bind(keywordLst[position])
            holder.setImage(searchImg)

            //최근 검색어 삭제
            holder.getViewBinding().deleteMySearch.setOnClickListener {
                //내장 디비에 저장된 최근 검색어 리스트를 기준으로 최신화
                Log.e("포지션 value",keywordLst[position])
                val check = GlobalApplication.userDataBase.deleteKeyword(keywordLst[position])

                if (check) {
                    keywordLst.removeAt(position)
                    Log.e("키워드리스트 사이즈",keywordLst.size.toString())

                    if (keywordLst.size == 0) { //최근 검색어 없음 표시하기 위해서
                        keywordLst.add("-1")
                        notifyDataSetChanged()
                    } else {
                      notifyDataSetChanged()
                    }
                }
            }
        }
        else if (holder is NoResentViewholder){
            holder.bind(keywordLst[position])
        }
    }

    fun updateList(list: MutableList<String>, searchImg: Int) {
        this.searchImg = searchImg
        keywordLst.clear()
        keywordLst.addAll(list.toMutableList())
    }

    override fun getItemViewType(position: Int): Int {
        return if (keywordLst[position] == "-1") {
            NO_SEARCH
        } else if(keywordLst[position] =="2"){
            NO_RELATIVE
        }
        else
            MY_SEARCH
    }
}