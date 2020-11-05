package com.adapter.alcholdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.Alchol_Component_Default
import com.adapter.viewholder.Alchol_Component_SRM
import com.adapter.viewholder.Alchol_Component_RecyclerView
import com.application.GlobalApplication
import com.model.alchol_detail.AlcholComponentData
import java.lang.RuntimeException

class AlcholComponentAdapter(val lst:MutableList<AlcholComponentData>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            GlobalApplication.COMPONENT_DEFAULT->{Alchol_Component_Default(parent)}
            GlobalApplication.COMPONENT_RECYCLERVIEW->{Alchol_Component_RecyclerView(parent)}
            GlobalApplication.COMPONENT_SRM->{Alchol_Component_SRM(parent)}
            else->{throw RuntimeException("알 수 없는 뷰타입 에러")}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Alchol_Component_Default -> {
                holder.bind(lst[position]) }
            is Alchol_Component_RecyclerView -> {
                holder.bind(lst[position]) }
            is Alchol_Component_SRM -> {
                holder.bind(lst[position]) }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun getItemViewType(position: Int): Int {
        return lst[position].type
    }
}