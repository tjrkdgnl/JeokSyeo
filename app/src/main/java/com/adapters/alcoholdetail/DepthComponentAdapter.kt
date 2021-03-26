package com.adapters.alcoholdetail

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapters.viewholder.DepthComponentViewholder
import com.vuforia.engine.wet.R

/**
 * 컴포넌트 안에서 표시될 리싸이클러뷰 어댑터이다.
 */
class DepthComponentAdapter(private val context: Context,
                            private val lst:MutableList<String>?, private var dummyCheck:Boolean) :RecyclerView.Adapter<DepthComponentViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepthComponentViewholder {
        return DepthComponentViewholder(parent)
    }

    override fun onBindViewHolder(holder: DepthComponentViewholder, position: Int) {
        if(dummyCheck){
            if(lst?.size ==1){
                holder.getViewBinding().commponentText.textSize =18f
            }

            //리스트가 두개 이상일 때, 중간에 리스트 요소들이 표시되도록 아이템의 넓이 셋팅
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