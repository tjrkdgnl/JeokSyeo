package com.adapter.alchol_rated

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholRatedViewHolder
import com.fragment.alchol_rated.Fragment_alcholRated

class AlcholRatedAdapter(private val lst:MutableList<String>,
private val smoothScrollPosition: Fragment_alcholRated.SmoothScrollListener
):RecyclerView.Adapter<AlcholRatedViewHolder>() {
    private  val checkList = mutableListOf<Boolean>()
    init {
        for(i in lst){
            checkList.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholRatedViewHolder {
        return AlcholRatedViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholRatedViewHolder, position: Int) {
        holder.bind(lst[position])

        holder.getViewBinding().ratedItemExpandableButton.setOnClickListener{
            if(!checkList[position] && !holder.getViewBinding().ratedItmeComment.isExpanded){
                checkList[position] =true
                holder.getViewBinding().ratedItmeComment.expand()
                smoothScrollPosition.moveScroll(position)

            }
            else{
                checkList[position] =false
                holder.getViewBinding().ratedItmeComment.collapse()
                smoothScrollPosition.moveScroll(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}