package com.adapter.alchol_category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholCategoryGridViewHolder
import com.adapter.viewholder.AlcholCategoryListViewHolder
import com.application.GlobalApplication
import com.model.alchol_category.AlcholList

class ListAdapter(private val lst:MutableList<AlcholList>):RecyclerView.Adapter<AlcholCategoryListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholCategoryListViewHolder {
        return AlcholCategoryListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholCategoryListViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarListRatingbar.rating = lst[position].review?.score!!
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun updateList(list:MutableList<AlcholList>){
        val newlist = mutableListOf<AlcholList>()
        for(newData in list.withIndex()){

            if(newData.index > GlobalApplication.PAGINATION_SIZE)
                break

            for(previousData in lst){
                if(newData.value.equals(previousData.alcholId)){

                }
                else{
                    newlist.add(newData.value)
                }
            }
        }
        lst.addAll(newlist)
        notifyDataSetChanged()
    }

    fun getLastAlcholId():String?{
        return lst.get(lst.size-1).alcholId
    }
}