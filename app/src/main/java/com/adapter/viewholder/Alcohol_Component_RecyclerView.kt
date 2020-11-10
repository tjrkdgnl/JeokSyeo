package com.adapter.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcoholdetail.DepthComponentAdapter
import com.base.BaseViewHolder
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

        //data.contents?.toMutableList()
        //데이터 사이즈에 대해서 더미 값 넣기.
        binding.compoentRecyclerView.adapter = DepthComponentAdapter(mutableListOf("미시간 캐스케이드","할러타우","팔레","테트낭","월래밋"))
        binding.compoentRecyclerView.setHasFixedSize(true)
        binding.compoentRecyclerView.layoutManager =LinearLayoutManager(parent.context)
    }
}