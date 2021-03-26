package com.adapters.search

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapters.viewholder.NoRelativeSearchViewHolder
import com.adapters.viewholder.NoResentViewholder
import com.adapters.viewholder.SearchViewHolder
import com.application.GlobalApplication
import com.fragments.search.SearchContract
import com.vuforia.engine.wet.R

class SearchAdapter(
    val context: Context,
    var keywordLst: MutableList<String>,
    private val searchInterface: SearchContract.BaseVIew
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val NO_SEARCH = -1
        const val MY_SEARCH = 1
        const val NO_RELATIVE = 2
    }

    private var searchImg: Int = R.mipmap.resent_timer //이미지에 따라서 delete 레이아웃 visible 여부 결정

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_SEARCH -> { //최근 검색어가 존재하지 않는 경우
                NoResentViewholder(parent)
            }
            MY_SEARCH -> { //검색어가 존재하지 않는 경우
                SearchViewHolder(parent, searchInterface)
            }
            NO_RELATIVE -> { // 연관 검색어가 존재하지 않는 경우
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
                val check = GlobalApplication.userDataBase.deleteKeyword(keywordLst[position])

                if (check) {
                    keywordLst.removeAt(position)

                    if (keywordLst.size == 0) { //최근 검색어 없음 표시하기 위해서
                        keywordLst.add("-1")
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetChanged()
                    }
                }
            }
        } else if (holder is NoResentViewholder) {
            holder.bind(keywordLst[position])
        }
    }

    fun updateList(list: MutableList<String>, searchImg: Int) {
        this.searchImg = searchImg
        keywordLst.clear()
        keywordLst.addAll(list.toMutableList())
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            keywordLst[position] == "-1" -> {
                NO_SEARCH
            }
            keywordLst[position] == "2" -> {
                NO_RELATIVE
            }
            else -> MY_SEARCH
        }
    }
}