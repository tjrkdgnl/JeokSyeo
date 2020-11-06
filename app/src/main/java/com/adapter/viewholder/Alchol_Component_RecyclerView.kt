package com.adapter.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcholdetail.DepthComponentAdapter
import com.base.BaseViewHolder
import com.model.alchol_detail.AlcholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholComponentItemRecyclerviewBinding

class Alchol_Component_RecyclerView(val parent:ViewGroup)
    :BaseViewHolder<AlcholComponentData,AlcholComponentItemRecyclerviewBinding>(R.layout.alchol_component_item_recyclerview,parent) {

    override fun bind(data: AlcholComponentData) {
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