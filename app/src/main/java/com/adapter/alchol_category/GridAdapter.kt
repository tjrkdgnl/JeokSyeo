package com.adapter.alchol_category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholCategoryGridViewHolder
import com.application.GlobalApplication
import com.model.alchol_category.AlcholList

class GridAdapter(private val lst:MutableList<AlcholList>):RecyclerView.Adapter<AlcholCategoryGridViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholCategoryGridViewHolder {
        return AlcholCategoryGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholCategoryGridViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarGridRatingbar.rating = lst[position].review?.score!!
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun updateList(list:MutableList<AlcholList>){
        val newList = mutableListOf<AlcholList>()
        newList.clear()

        for(newData in list.withIndex()){
            if(newData.index >GlobalApplication.PAGINATION_SIZE){
                break
            }

            for(previousData in lst){
                if(newData.value.equals(previousData.alcholId)){

                }
                else{
                    newList.add(newData.value)
                }
            }
        }
        lst.addAll(newList)
        notifyDataSetChanged()
    }

    fun changeSort(list:MutableList<AlcholList>){
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    fun getLastAlcholId():String?{
        if(lst.size>1)
            return lst.get(lst.size-1).alcholId
        else
            return null
    }
}