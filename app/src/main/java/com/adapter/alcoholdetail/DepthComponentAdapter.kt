package com.adapter.alcoholdetail

import android.app.ActionBar
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.DepthComponentViewholder
import com.vuforia.engine.wet.R

class DepthComponentAdapter(private val context: Context,
                            private val lst:MutableList<String>?, private var dummyCheck:Boolean) :RecyclerView.Adapter<DepthComponentViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepthComponentViewholder {
        return DepthComponentViewholder(parent)
    }

    override fun onBindViewHolder(holder: DepthComponentViewholder, position: Int) {
        if(dummyCheck){ // 한개일 때, 텍스트의 사이즈는 커져야 함.

            if(lst?.size ==1){
                val layoutParam=holder.getViewBinding().componentParentLayout.layoutParams
                layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT
                holder.getViewBinding().commponentText.textSize =18f
            }
            else if(lst?.size ==2){
                holder.itemView.layoutParams.height = context.resources.getDimension(R.dimen.component_recyclerview_height).toInt() / lst.size
                holder.getViewBinding().commponentText.textSize =16f
            }
        }

        lst?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return lst?.size ?:0
    }
}