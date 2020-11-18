package com.adapter.alcoholdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.Alcohol_Component_Default
import com.adapter.viewholder.Alcohol_Component_SRM
import com.adapter.viewholder.Alcohol_Component_RecyclerView
import com.application.GlobalApplication
import com.model.alcohol_detail.AlcoholComponentData
import java.lang.RuntimeException

class AlcoholComponentAdapter(val lst:MutableList<AlcoholComponentData>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            GlobalApplication.COMPONENT_DEFAULT->{Alcohol_Component_Default(parent)}
            GlobalApplication.COMPONENT_RECYCLERVIEW->{Alcohol_Component_RecyclerView(parent)}
            GlobalApplication.COMPONENT_SRM->{Alcohol_Component_SRM(parent)}
            else->{throw RuntimeException("알 수 없는 뷰타입 에러")}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Alcohol_Component_Default -> {
                holder.bind(lst[position]) }
            is Alcohol_Component_RecyclerView -> {
                holder.bind(lst[position]) }
            is Alcohol_Component_SRM -> {
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