package com.adapter.viewholder

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcoholdetail.DepthComponentAdapter
import com.base.BaseViewHolder
import com.model.alcohol_detail.Alcohol
import com.model.alcohol_detail.AlcoholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemRecyclerviewBinding

class Alcohol_Component_RecyclerView(val parent:ViewGroup)
    :BaseViewHolder<AlcoholComponentData,AlcoholComponentItemRecyclerviewBinding>(R.layout.alcohol_component_item_recyclerview,parent) {

    override fun bind(data: AlcoholComponentData) {
        binding.componentListItem = data
        binding.executePendingBindings()

        data.compo_image?.let {
            binding.componentBackground.setImageResource(it)
        }


        val lst = (data.contents as? List<String>)?.toMutableList()
        var dummyCheck =false
        if(lst?.size !=0 ){
            if(lst?.size!! <=2)   //가운데 정렬을 위해서 더미 값 두개 추가
                dummyCheck =true
            Log.e("리싸이클러뷰 높이",binding.compoentRecyclerView.layoutManager?.height.toString())
            binding.compoentRecyclerView.adapter = DepthComponentAdapter(parent.context,lst,dummyCheck)
            binding.compoentRecyclerView.setHasFixedSize(true)
            binding.compoentRecyclerView.layoutManager =LinearLayoutManager(parent.context)
        }
    }
}