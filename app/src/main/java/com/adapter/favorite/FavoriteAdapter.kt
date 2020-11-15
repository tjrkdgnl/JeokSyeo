package com.adapter.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.FavoriteViewHolder
import com.adapter.viewholder.NoFavoriteViewHolder
import com.model.favorite.AlcoholList
import java.lang.RuntimeException

class FavoriteAdapter(private var lst:MutableList<AlcoholList>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 ->{FavoriteViewHolder(parent)}
            -1 ->{NoFavoriteViewHolder(parent)}
            else ->{throw RuntimeException("알 수 없는 뷰타입 에러")}
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is FavoriteViewHolder)
            holder.bind(lst[position])
    }

    override fun getItemViewType(position: Int): Int {
        return  lst[position].type
    }
}