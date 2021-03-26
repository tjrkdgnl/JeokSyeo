package com.adapters.alcoholdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapters.viewholder.AlcoholComponentDefault
import com.adapters.viewholder.AlcoholComponentRecyclerView
import com.adapters.viewholder.AlcoholComponentSRM
import com.application.GlobalApplication
import com.model.alcohol_detail.AlcoholComponentData

class AlcoholComponentAdapter(val lst:MutableList<AlcoholComponentData>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            GlobalApplication.COMPONENT_DEFAULT->{AlcoholComponentDefault(parent)}
            GlobalApplication.COMPONENT_RECYCLERVIEW->{AlcoholComponentRecyclerView(parent)}
            GlobalApplication.COMPONENT_SRM->{AlcoholComponentSRM(parent)}
            else->{throw RuntimeException("알 수 없는 뷰타입 에러")}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AlcoholComponentDefault -> {
                holder.bind(lst[position]) }
            is AlcoholComponentRecyclerView -> {
                holder.bind(lst[position]) }
            is AlcoholComponentSRM -> {
                holder.bind(lst[position]) }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun getItemViewType(position: Int): Int {
        return lst[position].type
    }

    fun addComponent(lst:MutableList<AlcoholComponentData>){
        this.lst.addAll(lst)
    }

    fun deleteComponent(){
        val size = lst.size-1

        for(idx in size downTo 4){
            lst.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }
}